# Decisiones de diseño: Los Desinformaticos, TP Sokoban

Proceso de Desarrollo de Software.
Cada decisión sigue el formato: problema, alternativas, decisión, consecuencias, ubicación en el código y pregunta de defensa.

## Índice
- DD-001: Separación en capas (MVC) y herramienta de build (Maven)
- DD-002: Modelo base con responsabilidades únicas (SRP, Information Expert)

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

## DD-002: Modelo base con responsabilidades únicas (SRP, Information Expert)

- **Fecha:** 2026-10-22
- **Estado:** Propuesta
- **Patrón / principio:** SRP; GRASP Information Expert; Low Coupling / High Cohesion (sin patrón GoF)
- **Requisito relacionado:** Movimiento del jugador: no atraviesa paredes (requisito no confirmado)

### 1. Problema y contexto
Necesitamos un modelo mínimo para mover al jugador por un tablero con paredes, pisos y destinos, sin dibujar nada. Lo que va a cambiar después (cajas, terrenos resbaladizos, cerrojos) debe poder sumarse sin reescribir estas clases.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Clases chicas: `Posicion`, `Tablero`, `Jugador` + enums `Direccion` y `TipoCelda` | Cada una tiene una razón de cambio; se prueban sin GUI | Más archivos que una sola clase | (elegida) |
| Una clase `Juego` con matriz de `char` y `if` por carácter | Rápida de escribir | Mezcla carga, reglas y estado; se rompe al agregar elementos | Viola SRP y dificulta los patrones siguientes |
| Jerarquía de clases para cada celda (`Pared`, `Piso`, `Destino`) | Polimorfismo desde el inicio | Hoy solo hay un dato (¿es transitable?): sobreingeniería | Se reevalúa en la Etapa 5 (terrenos) |

### 3. Decisión
`Posicion` es un `record` inmutable que sabe calcular su vecina. `Tablero` conoce qué hay en cada celda y si se puede transitar (Information Expert). `Jugador` conoce su posición y decide si se mueve consultando al tablero. `Direccion` y `TipoCelda` son enums porque tienen un conjunto cerrado de valores. Usamos enums en lugar de clases por celda porque hoy no hay comportamiento que justifique polimorfismo; no se aplica ningún patrón GoF todavía.

### 4. Principios que favorece
- **SRP:** el tablero no se mueve, el jugador no conoce la matriz de celdas.
- **Information Expert:** quien tiene el dato responde la pregunta (`Tablero.esTransitable`).
- **Low Coupling:** `Jugador` solo depende de `Tablero` y `Posicion`; ninguna clase importa Swing (DD-001).

### 5. Consecuencias y costos
El `Tablero` copia la matriz recibida para que nadie la altere desde afuera. `TipoCelda` como enum deberá reemplazarse por una abstracción cuando los terrenos tengan comportamiento (resbaladizo); lo asumimos y se registrará como una decisión nueva.

### 6. Dónde está en el código
| Rol | Clase | Estado |
|---|---|---|
| Valor inmutable de coordenadas | `Posicion` | Implementada |
| Experto en celdas | `Tablero` | Implementada |
| Entidad móvil | `Jugador` | Implementada |
| Valores cerrados | `Direccion`, `TipoCelda` | Implementada |

### 7. Cómo se extiende
Para agregar un tipo de celda simple (por ejemplo, agua intransitable) basta un valor nuevo en `TipoCelda`. Cuando una celda necesite comportamiento propio, se pasa a una interfaz (ver Etapa 5).

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué `TipoCelda` es un enum y no una jerarquía de clases?
**Respuesta breve:** Hoy las celdas solo difieren en un dato (si se puede pisar). Una jerarquía sería sobreingeniería; cuando aparezca comportamiento propio, como el terreno resbaladizo, la reemplazamos por polimorfismo o Strategy.

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
| DD-002 | Borrador de la entrada, código del modelo base y tests | completar |
