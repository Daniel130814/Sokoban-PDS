# Decisiones de diseño: Los Desinformaticos, TP Sokoban

Proceso de Desarrollo de Software.
Cada decisión sigue el formato: problema, alternativas, decisión, consecuencias, ubicación en el código y pregunta de defensa.

## Índice
- DD-001: Separación en capas (MVC) y herramienta de build (Maven)
- DD-002: Modelo base con responsabilidades únicas (SRP, Information Expert)
- DD-003: Cajas polimórficas y `Nivel` como coordinador del empuje
- DD-004: Carga de niveles desde `.txt` con Simple Factory
- DD-005: Estados de la partida con un enum (State GoF descartado por ahora)

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

## DD-003: Cajas polimórficas y `Nivel` como coordinador del empuje

- **Fecha:** 2026-10-22
- **Estado:** Propuesta
- **Patrón / principio:** Polimorfismo (GRASP), OCP, LSP; GRASP Controller (de caso de uso) y Low Coupling
- **Requisito relacionado:** Empujar cajas: una sola a la vez, sin tirar de ellas (requisito no confirmado). Tres tipos de caja (normal, frágil, llave)

### 1. Problema y contexto
El jugador empuja cajas y el enunciado (según la skill, sin confirmar) pide varios tipos de caja que reaccionan distinto al ser empujadas. Lo que va a cambiar es el comportamiento por tipo de caja; lo que debe quedar estable es la regla de empuje (destino libre, una sola caja, sin tirar).

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `Caja` abstracta con `alSerEmpujada()` y subclases | Un tipo nuevo es una clase nueva; `Nivel` no cambia | Hoy hay una sola subclase, y el punto de variación está vacío | (elegida) |
| Un enum `TipoCaja` y `switch` en `Nivel` | Menos clases | Cada tipo nuevo obliga a modificar `Nivel` (viola OCP) | Se vuelve inmantenible con 3 tipos y reglas propias |
| Sin abstracción: una sola clase `Caja` hasta que haga falta | Mínimo código | Obliga a refactorizar `Nivel` en la Etapa 6 | Los tipos de caja son parte del alcance planificado |

### 3. Decisión
`Caja` es una clase abstracta que guarda su posición y define `alSerEmpujada()`. `CajaNormal` es la primera implementación y no hace nada extra. `Nivel` coordina la jugada (Controller de caso de uso): consulta al `Tablero` por las paredes, busca cajas, valida el empuje y llama a `alSerEmpujada()` sin conocer el tipo concreto. `Jugador` quedó sin lógica de movimiento (solo guarda su posición) porque ahora la regla depende también de las cajas, y no queremos que conozca otras entidades.

### 4. Principios que favorece
- **OCP:** una caja frágil o llave se agrega como subclase sin tocar `Nivel` (el test con una `Caja` anónima lo demuestra).
- **LSP:** cualquier subclase puede usarse donde se espera una `Caja`; el contrato es "me empujan y reacciono".
- **Polimorfismo (GRASP):** en lugar de `if (tipo == ...)`, cada tipo decide qué hacer.
- **Low Coupling:** `Jugador` y `Caja` no se conocen entre sí; `Nivel` los relaciona.

### 5. Consecuencias y costos
`alSerEmpujada()` hoy es un método vacío en la única subclase: es una previsión, no una necesidad ya resuelta. La hipótesis se valida en la Etapa 6; si las cajas frágiles y llave no usaran ese punto, se debe reevaluar. `Caja.irA` y `Jugador.irA` son visibles solo dentro del paquete para que nadie fuera de `Nivel` mueva piezas saltándose las reglas. Se cambió el comportamiento de la Etapa 1: `Jugador.mover` se eliminó y sus pruebas pasaron a `NivelTest`.

### 6. Dónde está en el código
| Rol | Clase | Estado |
|---|---|---|
| Abstracción | `Caja` | Implementada |
| Variante concreta | `CajaNormal` | Implementada |
| Variantes futuras | `CajaFragil`, `CajaLlave` | Planificadas |
| Coordinador de la jugada | `Nivel` | Implementada |

### 7. Cómo se extiende
Una caja frágil se agrega como `CajaFragil extends Caja` que descuente resistencia en `alSerEmpujada()`. `Nivel` no se modifica (aunque habrá que definir cómo se retira una caja rota; ver Etapa 6).

### 8. Pregunta de defensa
**Pregunta:** ¿Para qué una clase abstracta `Caja` si hoy solo existe `CajaNormal`?
**Respuesta breve:** El enunciado pide tres tipos de caja que reaccionan distinto al ser empujadas. Con la abstracción, `Nivel` no cambia al sumar un tipo nuevo; sin ella tendríamos un `switch` creciendo en la clase central. Reconocemos que hoy es una previsión y la validamos en la Etapa 6.

---

## DD-004: Carga de niveles desde `.txt` con Simple Factory

- **Fecha:** 2026-10-22
- **Estado:** Propuesta
- **Patrón / principio:** Simple Factory (idioma, no es un patrón GoF); OCP; DIP por inyección; GRASP Creator y Pure Fabrication
- **Requisito relacionado:** Niveles cargados desde archivos de texto, uno por nivel (requisito no confirmado)

### 1. Problema y contexto
Cada nivel es un archivo de texto con un carácter por elemento. El mapeo carácter→objeto cambia cada vez que se agrega un elemento (cajas frágiles, llave, muros...). Debe quedar estable el procedimiento de lectura y validación del archivo. El formato es libre (ambigüedad abierta), así que queda documentado acá.

Leyenda adoptada de forma provisoria: `#` pared, ` ` piso, `.` destino, `$` caja, `*` caja sobre destino, `@` jugador, `+` jugador sobre destino. Las filas más cortas se completan con pared. Se rechaza: carácter desconocido, ningún jugador o más de uno, y cantidad de cajas distinta a la de destinos.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `FabricaDeElementos` (Simple Factory) + `CargadorDeNivel` | La leyenda está en un solo lugar; el cargador no cambia al sumar un elemento | Una clase más; no es GoF | (elegida) |
| `switch` de caracteres dentro del cargador | Menos clases | Mezcla leer/validar con conocer la leyenda; crece con cada elemento | Viola SRP y OCP |
| Builder para armar el nivel por pasos | Útil con muchas variantes | Hoy el nivel se arma en un solo paso: sobreingeniería | Se reevalúa si crece la validación |
| Factory Method (GoF, subclases del cargador deciden qué crear) | Permite variar la creación por herencia | No hay variantes de cargador | No hay problema que lo justifique |

### 3. Decisión
`FabricaDeElementos` traduce un carácter a una celda (`TipoCelda`), una caja opcional o la marca de jugador. `CargadorDeNivel` recorre las líneas, pide a la fábrica los objetos, valida y arma el `Nivel`. La fábrica se inyecta por constructor. Hoy no hay una interfaz de fábrica porque solo existe una implementación; se introducirá si aparece una segunda (por ejemplo, un formato de nivel distinto o temas, donde evaluaríamos Abstract Factory).

### 4. Principios que favorece
- **SRP:** una clase conoce la leyenda, otra lee y valida.
- **OCP:** agregar `%` para caja frágil es tocar la fábrica, sin modificar el cargador (aunque la fábrica en sí sí se edita: el cambio queda localizado, no cerrado).
- **DIP (parcial):** el cargador recibe su fábrica desde afuera; todavía depende de una clase concreta.
- **GRASP Creator / Pure Fabrication:** la fábrica no existe en el dominio del juego; se crea para bajar el acoplamiento y concentrar la creación.

### 5. Consecuencias y costos
El `switch` de la fábrica crece con cada elemento: el patrón lo concentra, no lo elimina. Si crece mucho, se reemplaza por un registro carácter→creador. La validación de niveles imposibles (cajas atrapadas) no se hace; solo la estructural. Se agregó `Nivel.tablero()` porque los tests y luego la vista necesitan leer las celdas.

### 6. Dónde está en el código
| Rol | Clase | Estado |
|---|---|---|
| Fábrica (Simple Factory) | `FabricaDeElementos` | Implementada |
| Cliente de la fábrica | `CargadorDeNivel` | Implementada |
| Error de formato | `NivelInvalidoException` | Implementada |
| Recurso de ejemplo | `levels/nivel1.txt` | Implementada |
| Interfaz de fábrica | — | No planificada hasta tener una segunda implementación |

### 7. Cómo se extiende
Para la caja frágil: nuevo carácter en `FabricaDeElementos.crearCaja` y su clase `CajaFragil`; el cargador no cambia. Para otro nivel, un archivo nuevo en `src/main/resources/levels`.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué una Simple Factory y no Factory Method ni Builder?
**Respuesta breve:** Necesitamos centralizar la traducción carácter→objeto, que es lo que más va a cambiar. Factory Method exige subclases que decidan qué crear y acá no hay variantes de cargador; Builder se justifica con construcciones en varios pasos y hoy el nivel se arma de una vez. Reconocemos que Simple Factory no es un patrón GoF.

---

## DD-005: Estados de la partida con un enum (State GoF descartado por ahora)

- **Fecha:** 2026-10-22
- **Estado:** Propuesta
- **Patrón / principio:** Sin patrón GoF (State evaluado y descartado); SRP; Information Expert
- **Requisito relacionado:** Condición de victoria: todas las cajas en destinos, avanzando al siguiente nivel (requisito no confirmado)

### 1. Problema y contexto
Hay que detectar la victoria, pasar de nivel y terminar el juego. Esto requiere un estado de la partida (jugando, nivel completado, juego terminado) que cambie qué acciones se aceptan. Lo que cambia con frecuencia es incierto: hoy hay tres estados y dos acciones (mover, avanzar).

Decisión provisoria sobre una ambigüedad abierta: al completar el último nivel la partida pasa por `NIVEL_COMPLETADO` (para poder mostrar el resumen) y recién al avanzar queda en `JUEGO_TERMINADO`.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Enum `EstadoPartida` + clase `Partida` | Mínimo código; un único campo, sin combinaciones de banderas; fácil de leer y probar | Si los estados ganan comportamiento propio, los `if` crecen | (elegida) |
| State (GoF): una clase por estado con `mover` y `avanzar` | Elimina condicionales; cada estado tiene sus reglas y transiciones | Tres clases casi vacías hoy: sobreingeniería | Cada estado solo decide si acepta o ignora una acción |
| Banderas booleanas (`completado`, `terminado`) | Triviales | Combinaciones inválidas posibles | Es justo lo que State/enum evitan |

### 3. Decisión
`Partida` guarda la lista de niveles, el índice actual y un `EstadoPartida`. `mover` ignora la acción si no se está jugando y pasa a `NIVEL_COMPLETADO` cuando `Nivel.estaResuelto()` da verdadero. `avanzarDeNivel` solo vale en `NIVEL_COMPLETADO` y lanza `IllegalStateException` si se usa mal (error de programación, no una tecla ignorable). `Nivel` es el experto en saber si está resuelto. No aplicamos State GoF porque ningún estado tiene hoy comportamiento propio más allá de aceptar o rechazar.

### 4. Principios que favorece
- **Information Expert:** `Nivel` sabe si sus cajas están en destinos; `Partida` sabe en qué nivel y estado está.
- **SRP:** `Nivel` resuelve una jugada; `Partida` secuencia niveles.
- **YAGNI / honestidad de diseño:** no se usa un patrón sin un problema que lo exija.

### 5. Consecuencias y costos
`Partida` tiene condicionales sobre el estado. Es aceptable con tres estados. **Cuándo reconsiderar:** si se agregan menú, pausa o pantallas con entrada y reglas propias por estado (Etapa 8), o si los condicionales se repiten en más de dos métodos, se migra a State. Los niveles que recibe `Partida` son objetos mutables ya cargados: reiniciar un nivel requerirá volver a cargarlo o usar el Memento (Etapa 7).

### 6. Dónde está en el código
| Rol | Clase | Estado |
|---|---|---|
| Valores de estado | `EstadoPartida` | Implementada |
| Contexto / secuenciador | `Partida` | Implementada |
| Detección de victoria | `Nivel.estaResuelto()` | Implementada |
| Clases State (`EstadoJugando`, etc.) | — | No planificadas hasta que haga falta |

### 7. Cómo se extiende
Un estado nuevo (por ejemplo `PAUSADO`) es un valor más del enum y un chequeo en `mover`. Si hacen falta más de uno o dos, se extrae a State y se registra una decisión nueva que reemplaza a esta.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué no usaron el patrón State para los estados del juego?
**Respuesta breve:** Cada estado solo decide si acepta o ignora una acción; no tienen reglas propias. Con un enum el código es más corto y igual de claro. Si el menú o la pausa agregan comportamiento por estado, migramos a State y lo documentamos.

---

## Ambigüedades abiertas (a confirmar con el enunciado vigente / docente)
- Uso de Swing como biblioteca gráfica.
- ¿Qué es "un movimiento": cada paso del jugador o solo los empujes?
- Reglas del undo (cuántos pasos, usos consecutivos, qué restaura).
- Reglas del terreno resbaladizo, cajas frágiles y llave/cerrojo/muro.
- Formato del nivel `.txt` y cantidad de niveles.
- Qué pasa al terminar el último nivel (hoy: pasa por `NIVEL_COMPLETADO` y luego `JUEGO_TERMINADO`).
- Si las cajas llave y frágiles también deben terminar sobre destinos (hoy: todas las cajas).

---

## Registro de uso de IA

| Entrada | Qué se hizo con apoyo de IA | Qué hizo y validó el equipo |
|---|---|---|
| DD-001 | Borrador de la entrada, estructura de paquetes y `pom.xml` | completar |
| DD-002 | Borrador de la entrada, código del modelo base y tests | completar |
| DD-003 | Borrador de la entrada, código de cajas, `Nivel` y tests | completar |
| DD-004 | Borrador de la entrada, fábrica, cargador, nivel de ejemplo y tests | completar |
| DD-005 | Evaluación State vs. enum, `Partida`, `Nivel.estaResuelto()` y tests | completar |
