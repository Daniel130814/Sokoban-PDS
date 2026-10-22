# Decisiones de diseño: Los Desinformaticos, TP Sokoban

Proceso de Desarrollo de Software.
Cada decisión sigue el formato: problema, alternativas, decisión, consecuencias, ubicación en el código y pregunta de defensa.

## Índice
- DD-001: Separación en capas (MVC) y herramienta de build (Maven)

---

## DD-001: Separación en capas (MVC) y herramienta de build (Maven)

- **Fecha:** 2026-10-22
- **Estado:** Propuesta
- **Patrón / principio:** MVC; SRP; Low Coupling / High Cohesion
- **Requisito relacionado:** Estructura del proyecto; "el modelo no debe depender de la interfaz gráfica" (requisito no confirmado, ver abajo)

### 1. Problema y contexto
Antes de escribir reglas del juego hay que decidir dónde vive cada responsabilidad. Lo que cambia con frecuencia es la interfaz (imágenes, sonidos, HUD, eventualmente otra biblioteca gráfica). Lo que debe permanecer estable son las reglas de Sokoban. Además, el equipo (5 integrantes) necesita compilar y ejecutar pruebas de la misma manera en cualquier máquina.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Paquetes `model` / `view` / `controller` (MVC) | Reglas testeables sin ventana; la vista se puede cambiar sin tocar el modelo | Más paquetes e indirección desde el inicio | (elegida) |
| Todo junto en una clase de juego con Swing | Rápido de escribir | Reglas imposibles de probar sin GUI; cambia por cualquier motivo (SRP) | Dificulta probar y defender cada patrón |

Herramienta de build:
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Maven | `mvn test` igual para todos; dependencias (JUnit) automáticas; estructura estándar `src/main/java` | Un `pom.xml` más que mantener | (elegida) |
| `javac` + JUnit manual | Sin configuración extra | Scripts y classpath manuales, frágil entre máquinas | Menos reproducible |

### 3. Decisión
Organizamos el código en `sokoban.model`, `sokoban.view`, `sokoban.controller` y `sokoban.util`, con la regla de que **`model` no importa `javax.swing`/`java.awt`**. Usamos Maven con Java 21 y JUnit 5.

### 4. Principios que favorece
- **SRP:** reglas, dibujo y entrada del usuario cambian por razones distintas y viven en paquetes distintos.
- **Low Coupling:** el modelo no conoce a la vista; luego se conectarán mediante Observer.
- **High Cohesion:** cada paquete agrupa clases con un mismo propósito.

### 5. Consecuencias y costos
Más estructura antes de tener juego visible. Mitigación: los paquetes se llenan etapa por etapa y no se crean clases vacías. Riesgo: que alguien importe Swing en el modelo; se revisa con el escáner de la skill y en las revisiones.

### 6. Dónde está en el código
| Rol | Paquete | Estado |
|---|---|---|
| Model | `sokoban.model` | Planificada |
| View | `sokoban.view` | Planificada |
| Controller | `sokoban.controller` | Planificada |
| Utilidades | `sokoban.util` | Planificada |

### 7. Cómo se extiende
Cambiar de Swing a otra interfaz solo requiere una nueva vista y su controlador; `sokoban.model` no se toca.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué el modelo no puede depender de Swing?
**Respuesta breve:** Para poder probar las reglas sin abrir una ventana y para que un cambio en la interfaz no obligue a modificar la lógica del juego.

---

## Ambigüedades abiertas (a confirmar con el enunciado vigente / docente)
- Uso de Swing como biblioteca gráfica.
- ¿Qué es "un movimiento": cada paso del jugador o solo los empujes?
- Reglas del undo (cuántos pasos, usos consecutivos, qué restaura).
- Reglas del terreno resbaladizo, cajas frágiles y llave/cerrojo/muro.
- Formato del nivel `.txt` y cantidad de niveles.

---

## Registro de uso de IA

| Entrada | Qué se hizo con apoyo de IA | Qué hizo y validó el equipo |
|---|---|---|
| DD-001 | Borrador de la entrada, estructura de paquetes y `pom.xml` | completar |
