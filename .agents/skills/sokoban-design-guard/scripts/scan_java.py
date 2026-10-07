#!/usr/bin/env python3
"""
scan_java.py - Escáner heurístico de señales de diseño en código Java (TP Sokoban).

Uso:
    python scripts/scan_java.py <carpeta | archivo.java | archivo.zip>

Detecta *señales* (no veredictos) relacionadas con SOLID, GRASP y MVC:
  - Modelo que importa Swing/AWT (MVC)
  - Clases largas / métodos largos / demasiados métodos (SRP)
  - instanceof repetido o switch con muchos case (OCP, Polymorphism)
  - Campos públicos no finales (encapsulamiento)
  - Singleton / estado estático mutable (DIP, acoplamiento)
  - Campos o variables declarados con tipo concreto de colección (DIP)
  - catch vacíos (manejo de errores)
  - Métodos con muchos parámetros
  - Lógica en listeners de Swing (Controller)

Los resultados pueden tener falsos positivos: verificar siempre leyendo el código.
"""

import re
from contextlib import ExitStack
import sys
import tempfile
import zipfile
from pathlib import Path

MAX_CLASS_LINES = 300
MAX_METHOD_LINES = 40
MAX_METHODS_PER_CLASS = 15
MAX_PARAMS = 5
MAX_CASES = 4
MAX_INSTANCEOF = 2

MODEL_DIR_HINTS = ("model", "modelo", "domain", "dominio", "core", "logic", "logica")
SWING_IMPORT = re.compile(r"^\s*import\s+(static\s+)?(javax\.swing|java\.awt)\b", re.M)
INSTANCEOF = re.compile(r"\binstanceof\b")
CASE = re.compile(r"^\s*case\s+[^:]+:|^\s*case\s+[^-]+->", re.M)
PUBLIC_FIELD = re.compile(
    r"^\s*public\s+(?!static\s+final|final|class|interface|enum|record|abstract|static\s+void)"
    r"(?!.*\()[\w<>\[\], ?]+\s+\w+\s*(=.*)?;", re.M)
STATIC_MUTABLE = re.compile(
    r"^\s*(?:private|public|protected)?\s*static\s+(?!final)[\w<>\[\], ?]+\s+\w+\s*(=.*)?;", re.M)
GET_INSTANCE = re.compile(r"\bgetInstance\s*\(")
CONCRETE_TYPE = re.compile(
    r"\b(ArrayList|LinkedList|HashMap|LinkedHashMap|TreeMap|HashSet|LinkedHashSet|TreeSet|ArrayDeque|Stack)\s*<[^;=()]*>\s+\w+\s*(=|;)")
EMPTY_CATCH = re.compile(r"catch\s*\([^)]*\)\s*\{\s*\}")
LISTENER_CLASS = re.compile(r"\b(KeyListener|ActionListener|MouseListener|KeyAdapter|MouseAdapter)\b")
METHOD_DECL = re.compile(
    r"^\s*(?:public|protected|private)?\s*(?:static\s+)?(?:final\s+)?(?:abstract\s+)?(?:synchronized\s+)?"
    r"(?:<[^>]+>\s+)?[\w<>\[\], ?]+\s+(\w+)\s*\(([^)]*)\)\s*(?:throws [\w., ]+)?\s*\{", re.M)
CONTROL_WORDS = {"if", "for", "while", "switch", "catch", "synchronized", "return", "new", "else", "try"}


def collect_files(target: Path, resources):
    if target.suffix.lower() == ".zip":
        tmp = Path(resources.enter_context(tempfile.TemporaryDirectory(prefix="sokoban_scan_")))
        with zipfile.ZipFile(target) as zf:
            for entry in zf.infolist():
                name = entry.filename.replace("\\", "/")
                resolved = (tmp / name).resolve()
                if not resolved.is_relative_to(tmp.resolve()) or ":" in name:
                    raise ValueError(f"Ruta ZIP fuera del directorio temporal: {entry.filename}")
                if entry.is_dir() or Path(name).suffix.lower() != ".java":
                    continue
                resolved.parent.mkdir(parents=True, exist_ok=True)
                resolved.write_bytes(zf.read(entry))
        target = tmp
    if target.is_file():
        return [target], target.parent
    return sorted(p for p in target.rglob("*") if p.is_file() and p.suffix.lower() == ".java"), target


def strip_comments_and_strings(src: str) -> str:
    src = re.sub(r"/\*.*?\*/", lambda m: "\n" * m.group(0).count("\n"), src, flags=re.S)
    src = re.sub(r"//[^\n]*", "", src)
    src = re.sub(r'"(?:\\.|[^"\\])*"', '""', src)
    return src


def method_spans(src: str):
    """Devuelve lista (nombre, línea_inicio, líneas, n_params) aproximada, contando llaves."""
    spans = []
    for m in METHOD_DECL.finditer(src):
        name = m.group(1)
        if name in CONTROL_WORDS:
            continue
        params = m.group(2).strip()
        n_params = 0 if not params else params.count(",") + 1
        start_pos = m.end() - 1  # posición de '{'
        depth, i = 0, start_pos
        while i < len(src):
            c = src[i]
            if c == "{":
                depth += 1
            elif c == "}":
                depth -= 1
                if depth == 0:
                    break
            i += 1
        start_line = src.count("\n", 0, m.start()) + 1
        length = src.count("\n", m.start(), i) + 1
        spans.append((name, start_line, length, n_params))
    return spans


def is_model_path(path: Path) -> bool:
    return any(part.lower() in MODEL_DIR_HINTS for part in path.parts[:-1])


def scan_file(path: Path, root: Path):
    raw = path.read_text(encoding="utf-8", errors="replace")
    src = strip_comments_and_strings(raw)
    rel = path.relative_to(root) if root in path.parents or root == path.parent else path
    findings = []
    total_lines = raw.count("\n") + 1

    if is_model_path(path) and SWING_IMPORT.search(src):
        findings.append(("Alta", "MVC", "El modelo importa Swing/AWT (el modelo no debería conocer la vista)."))

    if total_lines > MAX_CLASS_LINES:
        findings.append(("Media", "SRP", f"Archivo largo ({total_lines} líneas): ¿tiene más de una responsabilidad?"))

    spans = method_spans(src)
    if len(spans) > MAX_METHODS_PER_CLASS:
        findings.append(("Media", "SRP/Cohesión", f"{len(spans)} métodos: revisar si la clase concentra responsabilidades."))
    for name, line, length, n_params in spans:
        if length > MAX_METHOD_LINES:
            findings.append(("Media", "SRP", f"Método '{name}' (línea ~{line}) de ~{length} líneas."))
        if n_params > MAX_PARAMS:
            findings.append(("Baja", "Diseño", f"Método '{name}' (línea ~{line}) con {n_params} parámetros: ¿falta un objeto?"))

    n_inst = len(INSTANCEOF.findall(src))
    if n_inst > MAX_INSTANCEOF:
        findings.append(("Alta", "OCP/Polymorphism", f"{n_inst} usos de instanceof: ¿condicionales por tipo en lugar de polimorfismo?"))

    n_case = len(CASE.findall(src))
    key_switch = "getKeyCode" in src or "getKeyChar" in src
    if n_case > MAX_CASES and not key_switch:
        findings.append(("Media", "OCP/Polymorphism", f"switch con {n_case} 'case' en el archivo: ¿se agrega un tipo nuevo tocando este código?"))

    for m in PUBLIC_FIELD.finditer(src):
        line = src.count("\n", 0, m.start()) + 1
        findings.append(("Media", "Encapsulamiento", f"Campo público no final (línea ~{line}): {m.group(0).strip()[:70]}"))

    for m in STATIC_MUTABLE.finditer(src):
        line = src.count("\n", 0, m.start()) + 1
        findings.append(("Media", "Estado global", f"Campo estático mutable (línea ~{line}): {m.group(0).strip()[:70]}"))

    if GET_INSTANCE.search(src):
        findings.append(("Baja", "Singleton", "Uso/definición de getInstance(): ¿está justificado el Singleton? Documentarlo."))

    for m in CONCRETE_TYPE.finditer(src):
        line = src.count("\n", 0, m.start()) + 1
        findings.append(("Baja", "DIP", f"Declaración con tipo concreto (línea ~{line}): preferir List/Map/Set/Deque."))
        break  # una sola alerta por archivo para no saturar

    if EMPTY_CATCH.search(src):
        findings.append(("Media", "Manejo de errores", "Bloque catch vacío: no ocultar errores (archivos de nivel, imágenes, sonido)."))

    if LISTENER_CLASS.search(src) and any(l > 25 for _, _, l, _ in spans):
        findings.append(("Media", "GRASP Controller", "Listener de Swing con lógica extensa: ¿reglas de juego dentro del controlador/vista?"))

    return rel, total_lines, findings


def main():
    if len(sys.argv) != 2:
        print(__doc__)
        sys.exit(1)
    target = Path(sys.argv[1])
    if not target.exists():
        print(f"No existe: {target}")
        sys.exit(1)

    with ExitStack() as resources:
        files, root = collect_files(target, resources)
        report(files, root)


def report(files, root):
    if not files:
        print("No se encontraron archivos .java.")
        return

    order = {"Alta": 0, "Media": 1, "Baja": 2}
    all_findings, total = [], 0
    for f in files:
        rel, lines, findings = scan_file(f, root)
        total += lines
        for sev, tag, msg in findings:
            all_findings.append((str(rel), order[sev], sev, tag, msg))

    print(f"Archivos analizados: {len(files)}  |  Líneas totales: {total}")
    print("Nota: señales heurísticas, verificar leyendo el código.\n")
    if not all_findings:
        print("Sin señales detectadas por el escáner (esto no garantiza un buen diseño).")
        return
    all_findings.sort(key=lambda finding: (finding[1], finding[0]))
    current = None
    for rel, _, sev, tag, msg in all_findings:
        if rel != current:
            print(f"\n== {rel}")
            current = rel
        print(f"  [{sev}] {tag}: {msg}")
    counts = {s: sum(1 for f in all_findings if f[2] == s) for s in ("Alta", "Media", "Baja")}
    print(f"\nResumen: {counts['Alta']} alta(s), {counts['Media']} media(s), {counts['Baja']} baja(s)")


if __name__ == "__main__":
    main()
