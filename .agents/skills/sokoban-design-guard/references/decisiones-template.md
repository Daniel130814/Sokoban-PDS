> Procedencia: material del paquete sokoban-design-guard.skill. Los requisitos, datos institucionales, exclusiones de patrones y ejemplos corresponden a su contexto original; confirmar su vigencia para este proyecto. Las instrucciones del usuario y SKILL.md prevalecen. Los ejemplos no acreditan implementación ni validación del equipo.

# Plantilla: registro de decisiones de diseño

El archivo acumulativo es `docs/apuntes/decisiones-de-diseno.md`. Sirve para tres cosas: alimentar el **informe técnico**, preparar la **defensa** y declarar el **uso de IA**. Cada entrada responde a la pregunta central: **¿por qué elegimos este patrón (o este principio) y no otro?**

## Contenido
1. Encabezado del archivo (solo al crearlo)
2. Plantilla de una entrada
3. Ejemplo completo
4. Registro de uso de IA
5. Criterios de calidad de una entrada

---

## 1. Encabezado del archivo

```markdown
# Decisiones de diseño: [Nombre del equipo], TP Sokoban

Proceso de Desarrollo de Software. [Institución y docente, si están confirmados].
Cada decisión sigue el formato: problema, alternativas, decisión, consecuencias, ubicación en el código y pregunta de defensa.

## Índice
- DD-001: [Título]
```

Actualizar el índice con cada entrada nueva.

## 2. Plantilla de una entrada

```markdown
## DD-XXX: [Título corto de la decisión]

- **Fecha:** AAAA-MM-DD
- **Estado:** Propuesta | Adoptada | Reemplazada por DD-YYY
- **Patrón / principio:** [Ej.: Memento; SRP; MVC]
- **Requisito relacionado:** [Ej.: "Deshacer movimientos"]

### 1. Problema y contexto
Qué necesitábamos resolver y qué restricciones teníamos (del enunciado o propias). Qué cambia con frecuencia y qué debe quedar estable.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| [Opción elegida] | … | … | (elegida) |
| [Opción B] | … | … | … |
| Sin patrón (solución directa) | … | … | … |

### 3. Decisión
Qué elegimos y por qué gana frente a las otras, en dos o tres frases con las palabras del equipo.

### 4. Principios que favorece
Mapear a SOLID/GRASP/MVC, explicando el vínculo concreto (no solo nombrarlos).

### 5. Consecuencias y costos
Qué mejora, qué se complica (más clases, indirección, curva de aprendizaje), qué riesgos quedan y cómo los mitigamos.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| [Ej.: Originator] | `Nivel` | Implementada |
| [Ej.: Caretaker] | `HistorialDeEstados` | Planificada |

### 7. Cómo se extiende
Un ejemplo concreto de cambio futuro que queda fácil (y qué archivos se tocarían).

### 8. Pregunta de defensa
**Pregunta:** [la que el profe probablemente haría]
**Respuesta breve:** [2-4 líneas que cualquier integrante pueda decir]
```

## 3. Ejemplo completo (ilustrativo; no adoptado en este proyecto)

```markdown
## DD-001: Undo mediante Memento

- **Fecha:** 2026-10-07
- **Estado:** Adoptada
- **Patrón / principio:** Memento
- **Requisito relacionado:** Deshacer movimientos (últimos 15, retroceso de 5, máximo 3 usos consecutivos)

### 1. Problema y contexto
El enunciado pide restaurar el estado del nivel 5 movimientos hacia atrás, con historial de 15 movimientos y máximo de tres usos consecutivos. El estado que debe volver incluye jugador, cajas (con la resistencia de las frágiles), muros abiertos/cerrados y contadores. La forma de guardar el estado es un detalle interno del modelo que no debe exponerse a la vista ni al controlador.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Memento | Restaura estado completo sin que cada acción sepa revertirse; encapsula el estado | Consume memoria por snapshot | (elegida) |
| Command con deshacer() | Granularidad por acción; permite replay | Cada comando debe revertir deslizamientos, roturas y muros: fácil de errar | Mayor riesgo de bugs con efectos en cadena |
| Copiar el tablero entero en cada jugada, sin patrón | Rápido de escribir | Mezcla la responsabilidad de guardar con el tablero; expone estructura interna | Viola SRP y encapsulamiento |

### 3. Decisión
Usamos Memento: `Nivel` (Originator) crea snapshots inmutables de su estado, y `HistorialDeEstados` (Caretaker) los guarda en una pila acotada a 15. Restaurar 5 pasos atrás es sacar 5 snapshots. Gana frente a Command porque el enunciado habla de restaurar estado, no de revertir acciones.

### 4. Principios que favorece
- **SRP:** el historial es responsabilidad de una clase propia, no del tablero.
- **Encapsulamiento / Information Expert:** solo `Nivel` sabe qué compone su estado.
- **OCP:** si se agrega un tipo de caja con estado, solo cambia lo que `Nivel` guarda en el snapshot.

### 5. Consecuencias y costos
Más memoria (15 snapshots pequeños: aceptable). Hay que garantizar copias profundas. Mitigación: snapshot con objetos inmutables y un test que modifica el estado vivo y verifica que el snapshot no cambia.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Originator | `Nivel` | Implementada |
| Memento | `EstadoNivel` | Implementada |
| Caretaker | `HistorialDeEstados` | Implementada |

### 7. Cómo se extiende
Para guardar y cargar partida (funcionalidad adicional posible) se serializa un `EstadoNivel`: no hay que tocar la lógica del juego.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Memento y no Command para el undo?
**Respuesta breve:** El requisito pide volver a un estado anterior, y con deslizamientos, cajas que se rompen y muros que se abren, revertir acción por acción es propenso a errores. Con Memento guardamos el estado completo y lo restauramos, y el historial queda en una clase aparte.
```

## 4. Registro de uso de IA

Al final del archivo, mantener esta sección. El enunciado exige declarar el uso de IA en el informe técnico.

```markdown
## Registro de uso de IA

| Entrada | Qué se hizo con apoyo de IA | Qué hizo y validó el equipo |
|---|---|---|
| DD-001 | Sugerencia de Memento frente a Command; borrador de la entrada | Elección final, implementación, tests, ajustes |
```

No inventar la columna "Qué hizo el equipo": preguntarla al equipo y registrar lo que respondan. Si no responden, dejar el campo marcado "completar".

## 5. Criterios de calidad de una entrada

- **Específica del proyecto:** nombra clases y requisitos reales, no frases genéricas de manual.
- **Comparativa:** al menos dos alternativas, incluida la solución sin patrón, con motivos reales de descarte.
- **Honesta sobre los costos:** si el patrón agrega complejidad, decirlo.
- **Vínculo claro con principios:** explica cómo favorece SOLID/GRASP, no solo los nombra.
- **Defendible oralmente:** la respuesta breve de la sección 8 la puede decir cualquier integrante.
- **Coherente con el código y con el diagrama de StarUML.** Si el código cambia, actualizar la entrada (o marcarla como reemplazada).
