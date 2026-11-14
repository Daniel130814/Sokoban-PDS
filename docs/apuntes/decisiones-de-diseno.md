# Decisiones de diseño: Los Desinformaticos, TP Sokoban

Proceso de Desarrollo de Software.
Cada decisión sigue el formato: problema, alternativas, decisión, consecuencias, ubicación en el código y pregunta de defensa.

## Índice
- DD-001: Separación en capas (MVC) y herramienta de build (Maven)
- DD-002: Modelo base con responsabilidades únicas (SRP, Information Expert)
- DD-003: Cajas polimórficas y `Nivel` como coordinador del empuje
- DD-004: Carga de niveles desde `.txt` con Factory Method para las cajas
- DD-005: Estados de la partida con un enum (State GoF descartado por ahora)
- DD-006: Terreno resbaladizo con Strategy
- DD-007: Cajas frágil y llave; cerrojo y muros con Observer (State descartado)
- DD-008: Deshacer y reiniciar con Memento
- DD-009: Vista, HUD y sonido con Observer; controlador sin Swing
- DD-010: Recursos gráficos detrás de una fachada (Facade)
- DD-011: Puntaje con Strategy
- DD-012: Guardar y cargar partida con Command (registro de acciones)
- DD-013: Logros con Observer y Strategy
- DD-014: Tema visual con FlatLaf y pantallas propias (solo capa de vista)
- DD-015: Cronómetro por nivel con Observer
- DD-016: Campaña por mundos con Composite
- DD-017: Modos de juego con Template Method

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
Organizamos el código en `sokoban.model`, `sokoban.view`, `sokoban.controller` y `sokoban.util`, con la regla de que **`model` no importa `javax.swing`/`java.awt`**. Usamos Maven con Java 17 y JUnit 5.

### 4. Principios que favorece
- **SRP:** reglas, dibujo y entrada del usuario cambian por razones distintas y viven en paquetes distintos.
- **Low Coupling:** el modelo no conoce a la vista; luego se conectarán mediante Observer.
- **High Cohesion:** cada paquete agrupa clases con un mismo propósito.

### 5. Consecuencias y costos
Más estructura antes de tener juego visible. Mitigación: los paquetes se llenan etapa por etapa y no se crean clases vacías. Riesgo: que alguien importe Swing en el modelo; se revisa con el escáner de la skill y en las revisiones.

### 6. Dónde está en el código
| Rol | Paquete | Estado |
|---|---|---|
| Model | `sokoban.model` (con subpaquetes `carga`, `enums`, `puntaje`) | Implementada |
| View | `sokoban.view` | Implementada |
| Controller | `sokoban.controller` (con `accion`) | Implementada |
| Otras capas surgidas después | `sokoban.audio`, `sokoban.logros`, `sokoban.persistencia`, `sokoban.exception` | Implementada |
| Utilidades | `sokoban.util` | No hizo falta: no existe |

Regla verificada por `ArquitecturaTest`: `model`, `controller`, `logros` y `persistencia` no importan Swing, AWT ni javax.sound.

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

**Actualización (DD-003):** `Jugador` ya no decide si se mueve; solo guarda su posición, y la jugada la coordina `Nivel`.

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
`alSerEmpujada()` hoy es un método vacío en la única subclase: es una previsión, no una necesidad ya resuelta. La hipótesis se validó en la Etapa 6 (DD-007): `CajaFragil` usa `alSerEmpujada()`, y `CajaLlave` necesitó además otro punto de variación (`alAsentarseEn`). `Caja.irA` y `Jugador.irA` son visibles solo dentro del paquete para que nadie fuera de `Nivel` mueva piezas saltándose las reglas. Se cambió el comportamiento de la Etapa 1: `Jugador.mover` se eliminó y sus pruebas pasaron a `NivelTest`.

### 6. Dónde está en el código
| Rol | Clase | Estado |
|---|---|---|
| Abstracción | `Caja` | Implementada |
| Variante concreta | `CajaNormal` | Implementada |
| Variantes agregadas después | `CajaFragil`, `CajaLlave` | Implementadas (ver DD-007) |
| Coordinador de la jugada | `Nivel` | Implementada |

### 7. Cómo se extiende
Una caja frágil se agrega como `CajaFragil extends Caja` que descuente resistencia en `alSerEmpujada()`. `Nivel` no se modifica (aunque habrá que definir cómo se retira una caja rota; ver Etapa 6).

### 8. Pregunta de defensa
**Pregunta:** ¿Para qué una clase abstracta `Caja` si hoy solo existe `CajaNormal`?
**Respuesta breve:** El enunciado pide tres tipos de caja que reaccionan distinto al ser empujadas. Con la abstracción, `Nivel` no cambia al sumar un tipo nuevo; sin ella tendríamos un `switch` creciendo en la clase central. Reconocemos que hoy es una previsión y la validamos en la Etapa 6.

---

## DD-004: Carga de niveles desde `.txt` con Factory Method para las cajas

- **Fecha:** 2026-10-22 (actualizada el 2026-11-14: la creación de cajas pasó de Simple Factory a Factory Method)
- **Estado:** Propuesta
- **Patrón / principio:** Factory Method (GoF) para las cajas; OCP; DIP por inyección; GRASP Creator y Pure Fabrication
- **Requisito relacionado:** Niveles cargados desde archivos de texto, uno por nivel (requisito no confirmado)

### 1. Problema y contexto
Cada nivel es un archivo de texto con un carácter por elemento. El mapeo carácter→objeto cambia cada vez que se agrega un elemento (cajas frágiles, llave, muros...). Debe quedar estable el procedimiento de lectura y validación del archivo. El formato es libre (ambigüedad abierta), así que queda documentado acá.

Leyenda adoptada de forma provisoria: `#` pared, ` ` piso, `.` destino, `$` caja, `*` caja sobre destino, `@` jugador, `+` jugador sobre destino. Las filas más cortas se completan con pared. Se rechaza: carácter desconocido, ningún jugador o más de uno, y cantidad de cajas distinta a la de destinos.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `FabricaDeElementos` con un `CreadorDeCaja` por tipo de caja (Factory Method) + `CargadorDeNivel` | La leyenda está en un solo lugar; sumar un tipo de caja es un creador nuevo, sin editar un `switch`; el cargador no cambia | Más clases chicas | (elegida) |
| `switch` de caracteres dentro del cargador | Menos clases | Mezcla leer/validar con conocer la leyenda; crece con cada elemento | Viola SRP y OCP |
| Builder para armar el nivel por pasos | Útil con muchas variantes | Hoy el nivel se arma en un solo paso: sobreingeniería | Se reevalúa si crece la validación |
| Simple Factory con un `switch` de caracteres (la versión anterior) | Una sola clase, sencillo | No es un patrón GoF; el `switch` crece con cada tipo de caja | Se reemplazó por Factory Method |
| Abstract Factory (familias de elementos) | Cubre variantes completas de reglas | No hay ninguna familia de productos que varíe junta | No hay problema que lo justifique |

### 3. Decisión
`FabricaDeElementos` traduce un carácter a una celda (`TipoCelda`), a la marca de jugador o a una caja. Las cajas se delegan en un `CreadorDeCaja`, que es el método de fábrica (`crearCaja(símbolo, posición)`): `CreadorDeCajaNormal` ('$' y '*'), `CreadorDeCajaFragil` ('%') y `CreadorDeCajaLlave` ('a' a 'i', donde la letra es el id). La fábrica guarda un mapa símbolo→creador, que por defecto trae esos tres y se puede reemplazar por constructor. `CargadorDeNivel` recorre las líneas, pide a la fábrica los objetos, valida y arma el `Nivel`; no cambió.

### 4. Principios que favorece
- **SRP:** una clase conoce la leyenda, otra lee y valida.
- **OCP:** un tipo de caja nuevo es un creador más y una entrada en el mapa; ni el cargador ni el resto de la fábrica se modifican (se prueba en `CreadoresDeCajaTest` con un creador inyectado).
- **DIP (parcial):** el cargador recibe su fábrica desde afuera; todavía depende de una clase concreta.
- **GRASP Creator / Pure Fabrication:** la fábrica no existe en el dominio del juego; se crea para bajar el acoplamiento y concentrar la creación.

### 5. Consecuencias y costos
Es una forma simplificada de Factory Method: los creadores son objetos que solo crean, no clases con otras operaciones que se heredan, y la selección por símbolo sigue en un mapa. Las celdas siguen en un `switch` en la fábrica porque son valores de un enum y no hay variación de comportamiento que justifique un creador. Hay tres clases chicas más. La validación de niveles imposibles (cajas atrapadas) no se hace; solo la estructural. Se agregó `Nivel.tablero()` porque los tests y luego la vista necesitan leer las celdas.

### 6. Dónde está en el código
| Rol | Clase | Estado |
|---|---|---|
| Creator (método de fábrica) | `CreadorDeCaja` | Implementada |
| Creadores concretos | `CreadorDeCajaNormal`, `CreadorDeCajaFragil`, `CreadorDeCajaLlave` | Implementada |
| Productos | `CajaNormal`, `CajaFragil`, `CajaLlave` | Implementada |
| Registro símbolo→creador | `FabricaDeElementos` | Implementada |
| Cliente de la fábrica | `CargadorDeNivel` | Implementada |
| Error de formato | `NivelInvalidoException` | Implementada |
| Recurso de ejemplo | `levels/nivel1.txt` | Implementada |

### 7. Cómo se extiende
Para una caja nueva (por ejemplo, pesada): su clase, un `CreadorDeCajaPesada` y su símbolo en el mapa de `FabricaDeElementos`; el cargador no cambia. Para otro nivel, un archivo nuevo en `src/main/resources/levels`.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Factory Method para las cajas y no un `switch`?
**Respuesta breve:** Lo que cambia es qué tipo de caja se crea para cada símbolo y ya hay tres tipos; con un creador por tipo, agregar uno nuevo no obliga a editar código existente. El cargador sigue sin saber qué cajas existen. Reconocemos que es una versión simplificada del patrón y que para las celdas dejamos el `switch` porque no hay variación de comportamiento. Builder no hace falta porque el nivel se arma de una vez, y Abstract Factory no aplica porque no hay familias de productos.

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

## DD-006: Terreno resbaladizo con Strategy

- **Fecha:** 2026-10-22
- **Estado:** Propuesta
- **Patrón / principio:** Strategy (GoF); OCP; GRASP Protected Variations y Polymorphism
- **Requisito relacionado:** Terreno resbaladizo: la caja se desliza hasta chocar o llegar a un espacio vacío (requisito no confirmado)

### 1. Problema y contexto
Dónde termina una caja empujada depende del terreno al que llega. Lo que varía es esa regla (normal, resbaladiza, y quizá otras); lo que debe quedar estable es el procedimiento de empuje de `Nivel`.

Reglas provisorias para las ambigüedades abiertas: (a) la caja que llega a hielo sigue hasta chocar con pared u otra caja, o hasta entrar a una celda que no es hielo (piso o destino), donde se detiene; (b) el deslizamiento completo cuenta como un único empuje (`alSerEmpujada` se llama una vez); (c) solo las cajas resbalan, no el jugador.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Strategy: cada celda tiene una `ReglaDeMovimiento` (`MovimientoNormal`, `Deslizamiento`) | `Nivel` no pregunta por tipos; terreno nuevo = estrategia nueva | Una interfaz y dos clases pequeñas | (elegida) |
| `if (celda.esResbaladiza())` en `Nivel` con bucle | Menos clases | Cada terreno nuevo (arena, cinta transportadora) agrega condicionales en `Nivel` | Viola OCP |
| Polimorfismo directo en una jerarquía `Terreno` | Parecido a Strategy | Obliga a reemplazar el enum `TipoCelda` entero hoy | Es un cambio más grande sin beneficio extra |

### 3. Decisión
`ReglaDeMovimiento` es la estrategia: dada la celda de llegada, devuelve la posición final de la caja. `MovimientoNormal` devuelve la misma celda; `Deslizamiento` mira la siguiente: si está libre, delega en la regla de esa celda, y si no, se detiene. Cada valor de `TipoCelda` guarda su regla, así que el terreno elige la estrategia y `Nivel` solo la ejecuta (contexto). La recursión hace que el hielo se detenga solo al llegar a piso o destino sin ningún `if` por tipo.

Esto cumple la promesa de DD-002 solo en parte: `TipoCelda` pasó de enum de datos a enum con comportamiento y se movió a `sokoban.model` para evitar un ciclo entre paquetes. No se creó una jerarquía `Terreno`; se reevalúa si los terrenos ganan estado propio.

### 4. Principios que favorece
- **OCP:** un terreno nuevo (por ejemplo, un empuje que mueve dos casillas) es una nueva regla y un valor del enum; `Nivel` no cambia.
- **Protected Variations:** la variación (regla de movimiento) queda detrás de una interfaz estable.
- **Polimorfismo (GRASP):** reemplaza los condicionales por tipo de terreno.

### 5. Consecuencias y costos
Más clases e indirección. `Deslizamiento` conoce a `Tablero` y usa un `Predicate` para saber si una celda está ocupada por una caja, lo que lo acopla a ambos. Las instancias de estrategia se crean una por constante del enum (son sin estado). Strategy vs. State: acá la estrategia la elige el contexto (el terreno) desde afuera, no cambia sola según el estado interno de la caja; por eso es Strategy y no State. Las cajas frágiles (Etapa 6) sí encajan mejor en State.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Strategy | `ReglaDeMovimiento` | Implementada |
| ConcreteStrategy | `MovimientoNormal`, `Deslizamiento` | Implementada |
| Context | `Nivel` (método `empujar`) | Implementada |
| Selección de la estrategia | `TipoCelda` (campo `reglaDeMovimiento`) | Implementada |

### 7. Cómo se extiende
Un terreno de arena que frena la caja a la mitad sería una nueva clase `ReglaDeMovimiento`, un valor en `TipoCelda` y un carácter en `FabricaDeElementos`.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Strategy y no un `if` para el terreno resbaladizo?
**Respuesta breve:** La regla de movimiento es lo que cambia con cada terreno. Con un `if` en `Nivel` cada terreno nuevo obligaría a modificarlo; con Strategy, `Nivel` solo ejecuta la regla de la celda y el terreno nuevo se agrega sin tocarlo. No es State porque la regla la elige el terreno desde afuera, no el estado interno de la caja.

---

## DD-007: Cajas frágil y llave; cerrojo y muros con Observer (State descartado)

- **Fecha:** 2026-10-22
- **Estado:** Propuesta
- **Patrón / principio:** Observer (GoF) para cerrojo→muros; polimorfismo, OCP y LSP para las cajas; State evaluado y descartado para la frágil
- **Requisito relacionado:** Cajas frágiles con resistencia; cajas llave que abren muros al colocarse sobre un cerrojo (requisito no confirmado)

### 1. Problema y contexto
Hay dos comportamientos nuevos. (1) La caja frágil pierde resistencia en cada empuje y se rompe al llegar a cero. (2) La caja llave, al quedar sobre su cerrojo, abre los muros asociados. `Nivel` debe seguir sin conocer tipos de caja concretos.

Reglas provisorias para ambigüedades abiertas:
- Resistencia inicial fija (`CajaFragil.RESISTENCIA_INICIAL = 3`), no definida por nivel. La caja se mueve en el empuje y, si llega a cero, desaparece del nivel (aunque haya quedado sobre un destino).
- Si se rompe una caja, hay menos cajas que destinos y el nivel es **irresoluble**: `Nivel.quedoIrresoluble()` lo informa para que la interfaz ofrezca reiniciar. Por eso `estaResuelto()` pasó a significar "todos los destinos están cubiertos" (con cajas y destinos iguales al cargar, es lo mismo que antes).
- Asociación por identificador numérico 1–9: caja llave `a`–`i`, cerrojo `1`–`9`, muro cerrado `A`–`I`; un cerrojo abre todos los muros de su mismo id. La caja frágil es `%`.
- Un muro abierto **queda abierto**, aunque la llave salga del cerrojo (más simple y evita dejar al jugador dentro de un muro que se cierra). Esto interactúa con el undo y se resuelve en la Etapa 7 incluyendo las celdas del tablero en el memento.
- Solo una caja llave con el mismo id activa el cerrojo; cualquier otra caja no.

### 2. Alternativas consideradas
Caja frágil:
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Entero `resistencia` en `CajaFragil` + polimorfismo | Mínimo y claro | Si aparecen estados con comportamiento, habrá que migrar | (elegida) |
| State (intacta / dañada / rota) | Cada estado con su comportamiento | Hoy los tres estados solo difieren en un número; sin efectos propios | Sobreingeniería: se reevalúa si se agregan imagen/sonido/efecto por estado |

Cerrojo y muros:
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Observer: `Cerrojo` (Subject) notifica a `Muro` (Observer) | `Nivel` no conoce a los muros; relación uno a muchos explícita; el mismo mecanismo servirá para avisar a la vista y al sonido (Etapa 8) | Más clases y wiring en el cargador | (elegida) |
| `Nivel` guarda un mapa id→muros y los abre directamente | Menos clases | `Nivel` acumula otra responsabilidad y conoce el detalle de los muros | Menos extensible |
| Mediator | Centraliza la coordinación | Más pesado que lo necesario | Observer alcanza |

### 3. Decisión
`Caja` ganó dos puntos de variación con comportamiento por defecto: `alAsentarseEn(Cerrojo)` y `estaRota()`. `CajaFragil` descuenta resistencia al ser empujada y se rompe en cero. `CajaLlave` activa el cerrojo si coincide el id. Después de cada empuje, `Nivel` le pide a la caja que reaccione sin preguntar su tipo. `Cerrojo` es el Subject (avisa una sola vez), `Muro` es el Observer que pide al `Tablero` abrir su celda, y `CargadorDeNivel` conecta cada cerrojo con los muros de su id.

### 4. Principios que favorece
- **OCP / LSP:** dos tipos de caja nuevos sin modificar el algoritmo de empuje; ambos respetan el contrato de `Caja`.
- **Low Coupling:** el cerrojo no conoce a los muros concretos, solo a la interfaz `ObservadorDeCerrojo`.
- **Polimorfismo (GRASP):** ni `instanceof` ni `switch` por tipo de caja en `Nivel`.

### 5. Consecuencias y costos
Aumentó la superficie: 7 clases/interfaces nuevas y el cargador se complicó (se extrajo un acumulador `Lectura`). `Tablero` dejó de ser inmutable: puede abrir muros, así que el Memento de la Etapa 7 deberá guardar celdas. `Caja` tiene ahora tres métodos "de gancho" y dos con implementación vacía o constante; es el precio de evitar el `instanceof`. Riesgo: niveles irresolubles por rotura; mitigación pendiente en la UI. Los símbolos `a`–`i`, `1`–`9`, `A`–`I` son arbitrarios y deberían confirmarse.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Subject | `Cerrojo` | Implementada |
| Observer (interfaz) | `ObservadorDeCerrojo` | Implementada |
| Observer concreto | `Muro` | Implementada |
| Caja con resistencia | `CajaFragil` | Implementada |
| Caja que activa cerrojos | `CajaLlave` | Implementada |
| Estados de la caja frágil | — | No planificados |

### 7. Cómo se extiende
Una puerta de otro tipo (por ejemplo, un muro que se abre y suena) es un nuevo `ObservadorDeCerrojo`. Para que la vista oiga la apertura se registra como observador sin tocar `Cerrojo` ni `Nivel`.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Observer para abrir los muros, y no State para la caja frágil?
**Respuesta breve:** El cerrojo avisa a cero, uno o varios muros sin conocerlos, y ese mismo mecanismo reutiliza la vista y el sonido. En cambio, la caja frágil solo cuenta resistencia: sus "estados" no tienen comportamiento propio, así que State sería sobreingeniería. Lo reevaluamos si cada estado necesita efectos distintos.

---

## DD-008: Deshacer y reiniciar con Memento

- **Fecha:** 2026-10-23
- **Estado:** Propuesta
- **Patrón / principio:** Memento (GoF); SRP; encapsulamiento
- **Requisito relacionado:** Deshacer movimientos: últimos 15, cada undo retrocede 5, máximo 3 usos consecutivos; reiniciar nivel (requisito no confirmado)

### 1. Problema y contexto
Hay que volver el nivel a un estado anterior. Lo que cambia durante el juego es mucho y variado: posición del jugador, cajas (con la resistencia de las frágiles y las cajas rotas), celdas del tablero (muros abiertos), cerrojos activos y contadores. La forma de guardar ese estado no debe filtrarse a la vista ni al controlador.

Reglas provisorias para las ambigüedades abiertas:
- "Un movimiento" es cada paso del jugador que se concreta, con o sin empuje (un intento bloqueado no cuenta ni se guarda).
- El historial guarda los últimos 15 estados; cada undo retira hasta 5; si hay menos de 5, retrocede lo que haya; sin historial no hace nada.
- "Consecutivos" significa sin un movimiento intermedio: mover reinicia la cuenta de usos consecutivos.
- El undo restaura también los contadores (movimientos y empujes), la resistencia y las cajas rotas, y los muros (que se vuelven a cerrar). El número de usos de undo no se restaura: se acumula por nivel y lo usará el puntaje.
- Reiniciar vuelve al estado inicial, vacía el historial y pone en cero los usos. Ni undo ni reinicio se permiten con el nivel completado.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Memento: `EstadoNivel` guardado en `HistorialDeEstados` | Restaura el estado completo sin que cada acción sepa revertirse; encapsula el estado | Consume memoria por snapshot; hay que cuidar las copias | (elegida) |
| Command con `deshacer()` por jugada | Granularidad por acción; sirve para replay | Revertir deslizamientos, roturas y muros uno por uno es propenso a errores | Mayor riesgo de bugs con efectos en cadena |
| Copiar el nivel entero sin patrón | Rápido | El historial conoce la estructura interna del nivel (rompe encapsulamiento) | Viola SRP y encapsulamiento |

### 3. Decisión
`Nivel` es el Originator: crea un `EstadoNivel` (Memento) con `guardarEstado()` y se restaura con `restaurar()`. `EstadoNivel` es inmutable y solo `Nivel` lee su contenido. `HistorialDeEstados` es el Caretaker: guarda hasta 15 y entrega el estado que corresponde, sin mirar adentro. `SesionDeNivel` aplica la política (5 pasos, 3 usos consecutivos) y el reinicio, que reutiliza el mismo mecanismo con el estado inicial. Cada caja captura su propio estado (`Caja.guardarEstado()`), con lo que `CajaFragil` agrega su resistencia sin que `Nivel` conozca el tipo.

### 4. Principios que favorece
- **Encapsulamiento:** quien guarda los estados no sabe qué contienen.
- **SRP:** `Nivel` juega, `HistorialDeEstados` almacena, `SesionDeNivel` aplica las reglas del undo, `Partida` secuencia niveles.
- **OCP / Polimorfismo:** una caja con estado nuevo solo sobrescribe `guardarEstado()`.

### 5. Consecuencias y costos
Cada movimiento guarda una copia de las celdas del tablero (pequeñas hoy; aceptable para 15 estados). Riesgo principal: copias superficiales. Se mitigó copiando las celdas al guardar y al restaurar, y con tests que modifican el estado vivo y restauran dos veces el mismo snapshot. Las cajas conservan su identidad (se restauran los mismos objetos), así que la vista no debe guardar referencias a la lista de cajas, sino consultar el nivel. El constructor de `EstadoNivel` tiene 8 parámetros (lo marca el escáner); se acepta porque es un contenedor de datos de construcción interna y no un punto de extensión. `Partida` recibe niveles mutables y reinicia con el estado inicial, lo que resuelve el costo anotado en DD-005. `EstadoCaja` usa lambdas para capturar el estado; es una forma compacta de memento, pero menos explícita al mostrarla en el diagrama de clases.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Originator | `Nivel` | Implementada |
| Memento | `EstadoNivel`, `EstadoCaja` | Implementada |
| Caretaker | `HistorialDeEstados` | Implementada |
| Política de undo y reinicio | `SesionDeNivel` | Implementada |
| Uso desde el juego | `Partida.deshacer()`, `reiniciarNivel()` | Implementada |

### 7. Cómo se extiende
Guardar y cargar partida (funcionalidad adicional posible) se apoya en `EstadoNivel`: hay que serializarlo y restaurarlo con `Nivel.restaurar`. Un replay se podría construir sobre el mismo historial o sumando Command.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Memento y no Command para el undo?
**Respuesta breve:** El requisito pide volver a un estado anterior, y con deslizamientos, cajas que se rompen y muros que se abren, revertir acción por acción es propenso a errores. Con Memento guardamos el estado completo y lo restauramos; el historial es una clase aparte que ni siquiera sabe qué hay dentro de cada estado.

---

## DD-009: Vista, HUD y sonido con Observer; controlador sin Swing

- **Fecha:** 2026-10-23
- **Estado:** Propuesta
- **Patrón / principio:** Observer (GoF); MVC; GRASP Controller; DIP; Singleton evaluado y descartado
- **Requisito relacionado:** Interfaz con HUD (nivel, movimientos, empujes, botones deshacer y reiniciar) y efectos de sonido; Swing (requisito no confirmado)

### 1. Problema y contexto
El tablero, el HUD y el sonido tienen que reaccionar cuando el modelo cambia (movimiento, empuje, rotura, cerrojo, undo, nivel completado), pero el modelo no puede depender de Swing ni de javax.sound (DD-001). Lo que cambia con frecuencia es qué se hace ante cada evento y cuántos interesados hay; lo estable son las reglas.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Observer: el modelo emite eventos concretos y los interesados se registran | El modelo no conoce a la vista; sumar un observador no toca el modelo; reutiliza el mecanismo ya usado en cerrojo→muro | Más clases; el orden de notificación importa | (elegida) |
| El controlador actualiza la vista y el sonido después de cada acción | Sin patrón, simple | El controlador tiene que acordarse de avisar a todos; cualquier cambio de estado fuera de él (undo, reinicio) se pierde | Acopla el controlador con vista y sonido |
| Vista que consulta al modelo con un temporizador | Sin eventos | Gasta ciclos y no distingue qué pasó (no sirve para el sonido) | No permite sonidos por evento |

### 3. Decisión
`ObservadorDeJuego` define ocho eventos concretos con implementación vacía por defecto (cada observador implementa solo los suyos). `Nivel` y `Partida` emiten por medio de `DifusorDeEventos`, que reparte a todos los observadores. `PanelDeTablero` y `PanelHud` se registran para repintar y actualizar contadores; `SonidosDelJuego` traduce eventos en efectos con un `ReproductorDeSonido` inyectado. `ControladorDeJuego` (GRASP Controller) recibe teclas y botones y llama al modelo; no importa Swing, y las teclas se asocian en `PanelDeTablero`. `Nivel` reutiliza el Observer del cerrojo: se registra como `ObservadorDeCerrojo` y lo convierte en el evento `cerrojoActivado`.

Singleton: no se usa para el audio. El reproductor se crea una sola vez en `Main` y se inyecta, lo que evita el estado global y permite probar `SonidosDelJuego` con un reproductor falso.

### 4. Principios que favorece
- **MVC / Low Coupling:** el modelo no importa `javax.swing` ni `javax.sound`.
- **OCP:** un observador nuevo (por ejemplo, estadísticas) se agrega sin modificar el modelo.
- **DIP:** `SonidosDelJuego` depende de la interfaz `ReproductorDeSonido`, no de javax.sound.
- **GRASP Controller:** un objeto recibe los eventos de la interfaz y los delega, sin lógica de juego propia.

### 5. Consecuencias y costos
Los eventos se emiten en el hilo que los provoca (el de Swing, al tocar una tecla); si algún día hubiera hilos, habría que sincronizar. Algunos eventos salen con el estado a medias (`cerrojoActivado` ocurre en medio del empuje), y por eso la vista solo repinta y no calcula nada a partir de ellos. Al empujar suenan `EMPUJE` y `PASO` juntos. `Nivel` ya tiene 16 métodos (jugada, memento, observadores): el escáner lo señala como cohesión a vigilar; una extracción posible es sacar el Memento o los eventos a otra clase. `VentanaPrincipal` muestra los diálogos con `invokeLater` para no bloquear el reparto de eventos. En `SelectorDeSprites` hay un `switch` con patrones sobre el tipo de caja: se acepta en la vista porque el modelo no debe conocer imágenes; un Visitor sería más pesado de lo que justifica.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Observer (interfaz) | `ObservadorDeJuego` | Implementada |
| Subject (reparto) | `DifusorDeEventos`, usado en `Nivel` y `Partida` | Implementada |
| Observers concretos | `PanelDeTablero`, `PanelHud`, `VentanaPrincipal`, `SonidosDelJuego` | Implementada |
| Controller | `ControladorDeJuego` | Implementada |
| Abstracción de audio | `ReproductorDeSonido`, `ReproductorJavaSound` | Implementada |

### 7. Cómo se extiende
Un contador de logros o estadísticas es un nuevo `ObservadorDeJuego` registrado en `Main`. Para cambiar el sonido (otro motor de audio) se escribe otra implementación de `ReproductorDeSonido`.

### 8. Pregunta de defensa
**Pregunta:** ¿Cómo evitan que el modelo dependa de la vista, y por qué no es un Singleton el sonido?
**Respuesta breve:** El modelo emite eventos por una interfaz y la vista, el HUD y el sonido se registran como observadores; el modelo nunca los nombra. El reproductor de sonido se crea una vez en `Main` y se inyecta: evitamos el estado global y podemos probar con un reproductor falso.

---

## DD-010: Recursos gráficos detrás de una fachada (Facade)

- **Fecha:** 2026-11-13
- **Estado:** Propuesta (reemplaza la versión del 2026-10-23, que usaba Flyweight)
- **Patrón / principio:** Facade (GoF); SRP; Low Coupling
- **Requisito relacionado:** Todos los elementos del tablero representados por imágenes (requisito no confirmado)

### 1. Problema y contexto
La vista necesita imágenes (muchas celdas comparten la misma) y fuentes. Cada pantalla recibía por separado `RepositorioDeImagenes` y `RepositorioDeFuentes`, así que conocía dos clases de infraestructura y los constructores crecían (`VentanaPrincipal` llegó a siete parámetros). Lo que cambia es de dónde y cómo se cargan los recursos; lo estable es que las pantallas piden "una imagen" o "una fuente" por nombre.

La versión anterior de esta decisión presentaba el repositorio de imágenes como Flyweight. Se reemplazó porque el equipo indicó que ese patrón no se vio en la materia: el código es una cache simple (un mapa nombre→imagen) y no hace falta defenderlo con un patrón que no se puede explicar.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `RecursosVisuales` (Facade) delante de los dos repositorios | Una sola dependencia para la vista; las pantallas no saben cómo se carga cada recurso; menos parámetros | Una clase más; delega sin lógica propia | (elegida) |
| Dejar los dos repositorios y llamarlos "cache" sin patrón | Cero cambios de código | Seguimos pasando dos dependencias a todas las pantallas | Válida, pero no mejora el acoplamiento |
| Un repositorio único que cargue imágenes y fuentes | Una sola clase | Mezcla dos formatos y dos reglas de respaldo (la fuente tiene una de reserva) | Viola SRP |

### 3. Decisión
`RecursosVisuales` expone `imagen(nombre)` y `fuente(nombre, tamaño)` y delega en `RepositorioDeImagenes` y `RepositorioDeFuentes`, que conservan su cache interna. `Main` crea una `RecursosVisuales` y la recibe `VentanaPrincipal`, que la reparte a `PanelHud`, `PanelDeTablero`, `PantallaCentrada`, `BotonEspacial` y las demás pantallas. `SelectorDeSprites` sigue decidiendo qué nombre de imagen corresponde a cada elemento del modelo.

### 4. Principios que favorece
- **Low Coupling:** las pantallas dependen de una clase y no de dos repositorios.
- **SRP:** cargar y guardar en cache cada tipo de recurso sigue siendo una responsabilidad aparte.
- **MVC:** el modelo no conoce ninguna imagen ni fuente.

### 5. Consecuencias y costos
La fachada no agrega lógica: si crece solo para reenviar métodos, sería ruido. Los repositorios siguen siendo públicos y los tests de cache los usan directo, así que la fachada no impide saltársela. La cache nunca se vacía (aceptable: hay pocas imágenes y fuentes) y no se midió la mejora real. Si se agregan temas gráficos, `RecursosVisuales` es el lugar donde elegirlos (Abstract Factory).

### 6. Dónde está en el código
| Rol en el patrón | Clase | Estado |
|---|---|---|
| Facade | `RecursosVisuales` | Implementada |
| Subsistema (imágenes) | `RepositorioDeImagenes` | Implementada |
| Subsistema (fuentes) | `RepositorioDeFuentes` | Implementada |
| Clientes | `VentanaPrincipal`, `PanelHud`, `PanelDeTablero`, `Pantalla*`, `BotonEspacial` | Implementada |

### 7. Cómo se extiende
Una imagen nueva es un PNG en `resources/images` y una entrada en `SelectorDeSprites`. Un tipo de recurso nuevo (por ejemplo, íconos vectoriales) se suma como otro subsistema detrás de la misma fachada sin tocar las pantallas.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué una fachada si ya tenían los repositorios?
**Respuesta breve:** Porque las pantallas no tienen por qué saber que hay dos clases distintas ni cómo se carga cada recurso: piden una imagen o una fuente por nombre a un único punto de entrada. Reduce los parámetros y el acoplamiento sin tocar la cache. Reconocemos que la fachada hoy solo delega.

---

## DD-011: Puntaje con Strategy

- **Fecha:** 2026-10-23
- **Estado:** Propuesta
- **Patrón / principio:** Strategy (GoF); GRASP Protected Variations; OCP; DIP
- **Requisito relacionado:** Al terminar cada nivel, resumen con movimientos, empujes, uso del undo y puntaje final; el criterio lo define el equipo (requisito no confirmado)

### 1. Problema y contexto
El criterio de puntaje es libre y es probable que se ajuste mientras se prueba el juego (cuánto pesa un movimiento, un empuje o un deshacer). Lo estable son los datos del nivel y el momento en que se calcula.

Criterio adoptado de forma provisoria (`PuntajePorEficiencia`): 1000 puntos de base, menos 10 por movimiento, 5 por empuje y 50 por cada uso de deshacer, con piso en 0. Se penaliza más el deshacer para que usarlo tenga un costo visible; los números son un primer valor para probar, no una decisión final.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Strategy: `EstrategiaDePuntaje` inyectada en `Partida` | Cambiar o probar un criterio no toca la partida; se prueba con una función de una línea | Una interfaz y un record más; hoy hay una sola implementación real | (elegida) |
| Clase `CalculadorDePuntaje` concreta | Más simple, sin interfaz | Cambiar el criterio obliga a editar la clase que usa `Partida`; no se puede probar con un criterio falso | Menos flexible para algo que se espera ajustar |
| Fórmula dentro de `Partida` | Sin clases nuevas | Mezcla secuenciar niveles con puntuar (SRP) | Dificulta probar y cambiar la fórmula |

### 3. Decisión
`EstrategiaDePuntaje` recibe `EstadisticasDeNivel` (movimientos, empujes, usos de deshacer) y devuelve un entero. `Partida` es el contexto: arma las estadísticas con `Nivel` y `SesionDeNivel` y delega el cálculo, devolviendo un `ResumenDeNivel` en `resumenDelNivel()`, que solo puede pedirse con el nivel completado. La estrategia se inyecta por constructor desde `Main` (hay una sobrecarga sin estrategia que usa la de eficiencia, para los tests). `VentanaPrincipal` muestra el resumen con el puntaje final.

Honestidad sobre el patrón: hoy existe una única estrategia real. El valor está en que el criterio quedó aislado e intercambiable, lo que un test ya demuestra con una estrategia de una línea; si el equipo sabe que nunca cambiará la fórmula, bastaba una clase simple.

### 4. Principios que favorece
- **Protected Variations / OCP:** la parte que probablemente cambie (la fórmula) queda detrás de una interfaz estable.
- **DIP:** `Partida` depende de la abstracción `EstrategiaDePuntaje`.
- **SRP:** `Partida` secuencia niveles; el criterio de puntaje vive aparte.

### 5. Consecuencias y costos
Una interfaz y dos records más para una sola implementación. La sobrecarga del constructor con estrategia por defecto esconde la dependencia en los tests; `Main` la inyecta explícitamente. El puntaje se calcula por nivel; no hay total acumulado de la partida. Los valores de penalización son arbitrarios y no se validaron con jugadores.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Strategy | `EstrategiaDePuntaje` | Implementada |
| ConcreteStrategy | `PuntajePorEficiencia` | Implementada |
| Context | `Partida` (`resumenDelNivel()`) | Implementada |
| Datos de entrada y salida | `EstadisticasDeNivel`, `ResumenDeNivel` | Implementada |
| Segundas estrategias (por ejemplo, solo por movimientos) | — | No planificadas |

### 7. Cómo se extiende
Un criterio nuevo es una clase que implementa `EstrategiaDePuntaje`, y se elige en `Main`. Si hace falta otro dato (por ejemplo, tiempo), se agrega a `EstadisticasDeNivel` sin tocar las estrategias existentes que no lo usen.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Strategy para el puntaje, si hay una sola fórmula?
**Respuesta breve:** El enunciado deja el criterio libre y lo vamos a ajustar al probar, así que lo aislamos detrás de una interfaz: cambiarlo no toca la partida y lo probamos con un criterio falso. Reconocemos que hoy hay una sola implementación; si la fórmula nunca cambiara, una clase simple hubiera alcanzado.

---

## DD-012: Guardar y cargar partida con Command (registro de acciones)

- **Fecha:** 2026-10-23
- **Estado:** Propuesta
- **Patrón / principio:** Command (GoF); SRP; OCP. Funcionalidad adicional 1
- **Requisito relacionado:** Dos funcionalidades adicionales que apliquen patrones de diseño (requisito no confirmado; la elección de estas dos es una propuesta del equipo de desarrollo pendiente de validar)

### 1. Problema y contexto
Se quiere guardar una partida en un archivo y retomarla después. Hay que decidir qué se guarda. El `EstadoNivel` del Memento (DD-008) es opaco a propósito: solo `Nivel` lee su contenido y apunta a objetos del modelo. Serializarlo exigiría abrirlo, justo lo que se evitó. En cambio, el juego es determinista: el mismo nivel y las mismas acciones del jugador producen siempre el mismo estado.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| Command: cada acción es un objeto `Accion`; se guarda el registro y se reproduce | El modelo no cambia; incluye undo, reinicio y avance de nivel; no abre el Memento; sirve de base para un replay | Cargar reproduce toda la partida (lento solo si fuera muy larga); archivo ligado al orden de los niveles | (elegida) |
| Serializar `EstadoNivel` (Memento) | Carga instantánea | Obliga a exponer el contenido del Memento y a identificar cada caja al restaurar; pierde el historial de undo | Rompe el encapsulamiento que se buscó en DD-008 |
| Lista de enums y un `switch` para reproducirla | Menos clases | El `switch` crece con cada acción nueva | Menos extensible |

### 3. Decisión
`Accion` (Command) tiene `ejecutar(Partida)`, que dice si tuvo efecto, y `comoTexto()`. Hay cuatro acciones: `Mover`, `Deshacer`, `Reiniciar` y `Continuar`. `ControladorDeJuego` es el invocador: crea la acción, la ejecuta y registra solo las que tuvieron efecto, de modo que una tecla contra una pared no ensucia el archivo. `GuardadoDePartida` escribe y lee el registro (un encabezado y una acción por línea). Para cargar, `Partida.reconstruir` deja los niveles como al empezar usando sus estados iniciales (el Memento, reutilizado), reproduce las acciones sin avisar a los observadores y avisa una sola vez que cambió el nivel. Sin ese silencio, cada paso reproducido abriría el diálogo de nivel completado o sonaría.

La lectura y la validación del archivo se hacen antes de tocar la partida: un archivo inválido lanza `GuardadoInvalidoException` y la partida en curso queda como estaba (hay un test).

### 4. Principios que favorece
- **SRP:** quien guarda no ejecuta; quien ejecuta no sabe de archivos.
- **OCP:** una acción nueva es una clase más y una línea en el mapa de `GuardadoDePartida`.
- **Encapsulamiento:** el Memento sigue siendo opaco.

### 5. Consecuencias y costos
Se usa Command solo para registrar y reproducir: ninguna acción implementa `deshacer()` porque eso ya lo resuelve el Memento. El valor del patrón es moderado: la alternativa sin patrón (lista de enums) también hubiera funcionado. El archivo `partida-guardada.txt` queda ligado al orden y contenido de los niveles; si se editan los niveles, un guardado viejo puede dar otro resultado (las acciones que ya no se pueden ejecutar se ignoran). Los logros (DD-013) no se reconstruyen al cargar porque la reproducción es silenciosa. El guardado es un solo archivo fijo en el directorio de ejecución; no hay selector de archivos. El `JOptionPane` y los botones de la vista no tienen pruebas automáticas.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Command | `Accion` | Implementada |
| Comandos concretos | `Mover`, `Deshacer`, `Reiniciar`, `Continuar` | Implementada |
| Invoker / registro | `ControladorDeJuego` | Implementada |
| Receiver | `Partida` | Implementada |
| Persistencia del registro | `GuardadoDePartida` | Implementada |
| Reconstrucción silenciosa | `Partida.reconstruir`, `DifusorDeEventos.silenciar` | Implementada |

### 7. Cómo se extiende
Un replay visible (reproducir paso a paso con pausa) usa el mismo registro, ejecutando una acción por tick con los observadores activos. Una acción nueva (por ejemplo, pausa) es una clase `Accion` y una entrada en el mapa de lectura.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Command y no guardar directamente el estado con el Memento?
**Respuesta breve:** Nuestro Memento es opaco a propósito y serializarlo obligaría a abrirlo. Como el juego es determinista, alcanza con guardar las acciones del jugador y reproducirlas desde el estado inicial. Command nos da esas acciones como objetos que se registran, se guardan y se ejecutan de la misma manera. Reconocemos que acá solo registra y reproduce, no revierte.

---

## DD-013: Logros con Observer y Strategy

- **Fecha:** 2026-10-23
- **Estado:** Propuesta
- **Patrón / principio:** Observer (GoF) y Strategy (GoF); OCP; Low Coupling. Funcionalidad adicional 2
- **Requisito relacionado:** Dos funcionalidades adicionales que apliquen patrones de diseño (requisito no confirmado; elección pendiente de validar por el equipo)

### 1. Problema y contexto
Se quiere dar logros ("Completá un nivel sin deshacer", "Abrí un muro con una caja llave") sin que el modelo sepa nada de ellos. Lo que cambia con frecuencia es el catálogo de logros y sus condiciones; lo estable es el flujo de eventos del juego.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `Logros` observa los eventos y cada `Logro` trae su criterio (`CriterioDeLogro`) | El modelo no cambia; un logro nuevo es una línea del catálogo | Dos patrones juntos; los criterios dependen de los contadores que haya | (elegida) |
| Contadores y `if` dentro del modelo (`Nivel`, `Partida`) | Sin clases nuevas | Mezcla reglas del juego con premios; el modelo crece | Viola SRP y OCP |
| Un solo observador con un `switch` por logro | Menos piezas | El `switch` crece con cada logro | Menos extensible |

### 3. Decisión
`Logros` es un Observer: implementa `ObservadorDeJuego`, acumula `EstadisticasDeJuego` (empujes, cajas rotas, cerrojos activados, niveles completados y niveles sin deshacer) y, después de cada evento, evalúa los logros todavía no desbloqueados. Cada `Logro` es un registro con nombre, descripción y un `CriterioDeLogro` (Strategy): una función que decide si se cumple a partir de las estadísticas. El catálogo estándar son cinco logros definidos con lambdas. El HUD se registra para avisar cuando se desbloquea uno y muestra "Logros: n/5". Para saber si un nivel se completó sin deshacer, `Logros` consulta a `Partida.resumenDelNivel()`.

### 4. Principios que favorece
- **OCP:** un logro nuevo se agrega al catálogo sin tocar `Logros` ni el modelo (un test usa un catálogo propio).
- **Low Coupling:** el modelo no conoce los logros; solo emite eventos.
- **SRP:** `Logros` acumula y desbloquea; cada criterio decide por sí mismo.

### 5. Consecuencias y costos
El Strategy es funcional (lambdas) y conviven dos usos de Strategy en el proyecto (puntaje y logros), además de varios Observer: es coherente pero repite patrones, lo cual el equipo debería poder justificar en cada caso. Los logros se pierden al cerrar el juego (no se persisten) y no se reconstruyen al cargar una partida, porque la reproducción es silenciosa; es una limitación asumida. Al reiniciar un nivel los contadores de logros no se descuentan: un empuje cuenta aunque después se deshaga o se reinicie. Los criterios de los logros leen contadores públicos de `EstadisticasDeJuego`; si un criterio necesita otro dato, hay que agregar el contador.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Observer concreto | `Logros` | Implementada |
| Strategy | `CriterioDeLogro` | Implementada |
| Estrategias concretas | Lambdas en `CatalogoDeLogros.estandar()` | Implementada |
| Datos y contadores | `Logro`, `EstadisticasDeJuego` | Implementada |
| Vista del logro | `PanelHud` (etiqueta de logros) | Implementada |

### 7. Cómo se extiende
Un logro nuevo ("Cinco niveles sin deshacer") es otra línea en `CatalogoDeLogros`. Un logro persistente se puede lograr guardando los nombres desbloqueados en un archivo, sin tocar el modelo.

### 8. Pregunta de defensa
**Pregunta:** ¿Dónde está el Strategy en los logros y por qué un Observer?
**Respuesta breve:** `Logros` escucha los eventos que el modelo ya emite, así que el juego no sabe que existen. Cada logro lleva su propio criterio como una estrategia, y por eso un logro nuevo es una línea en el catálogo. Es un Strategy funcional con lambdas, y lo decimos así.

---

## DD-014: Tema visual con FlatLaf y pantallas propias (solo capa de vista)

- **Fecha:** 2026-10-28
- **Estado:** Propuesta
- **Patrón / principio:** Sin patrón nuevo; Observer (DD-009) y Facade (DD-010) reutilizados; SRP; MVC
- **Requisito relacionado:** Interfaz Swing con HUD (requisito no confirmado); la estética no se evalúa

### 1. Problema y contexto
La interfaz usaba el aspecto por defecto de Swing y diálogos `JOptionPane`. Se quiere una presentación coherente con el tema espacial sin que el cambio llegue a las reglas del juego.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| FlatLaf sobre Swing + dibujo propio | Mantiene Swing (DD-001/DD-009), una dependencia chica, el cambio queda en `view` | Dibujo manual del HUD | (elegida) |
| JavaFX | Gráficos y animación más ricos, CSS | Reescribe toda la vista y las pruebas, agrega módulos y plugin de build, y deja dos toolkits de UI en transición | Costo desproporcionado; la estética no se evalúa y Swing es el supuesto del TP |
| Look and Feel por defecto | Sin cambios | Aspecto desentonado | No cumple el objetivo visual |

### 3. Decisión
`Main` instala `FlatDarkLaf`. `PanelHud` se dibuja en `paintComponent`; los fondos e íconos salen de `RepositorioDeImagenes` y las fuentes de `RepositorioDeFuentes`. Menú, nivel completado y fin de juego son paneles en un `CardLayout` de `VentanaPrincipal`, que reacciona a los eventos existentes. El destello (`EfectoDeDestello`, con `javax.swing.Timer`) responde a `cajaSeRompio` y `cerrojoActivado`.

### 4. Principios que favorece
- **MVC / Low Coupling:** `model`, `controller`, `logros` y `persistencia` quedaron sin cambios; `ArquitecturaTest` sigue verificando que no importen Swing.
- **SRP:** una clase por pantalla, efecto y repositorio.
- **OCP:** un observador visual nuevo no toca el modelo.

### 5. Consecuencias y costos
Una dependencia nueva (FlatLaf). El destello cubre todo el tablero porque los eventos no llevan posición. El HUD muestra los deshaceres **usados**, no los restantes: el límite de usos consecutivos es privado en `SesionDeNivel` y mostrarlo exigiría un getter en el modelo. Tras el fin del juego solo se puede cargar una partida guardada o salir: una "nueva partida" requeriría un método en `ControladorDeJuego` para no desincronizar el registro de acciones. `RepositorioDeFuentes` repite la idea de cache de imágenes sin ser un patrón nuevo (queda detrás de la fachada de DD-010). Los diálogos de Guardar/Cargar y "caja rota" siguen siendo `JOptionPane`.

### 6. Dónde está en el código
| Rol | Clase | Estado |
|---|---|---|
| Tema | `Main` (`FlatDarkLaf`) | Implementada |
| Cache de fuentes | `RepositorioDeFuentes` | Implementada |
| Fondo, HUD y botones | `PanelEspacial`, `PanelHud`, `BotonEspacial` | Implementada |
| Pantallas | `PantallaDeMenu`, `PantallaNivelCompletado`, `PantallaFinDeJuego` (base `PantallaCentrada`) | Implementada |
| Feedback visual | `EfectoDeDestello` en `PanelDeTablero` | Implementada |

### 7. Cómo se extiende
Otro tema es otra carpeta de recursos y otro `LookAndFeel`; una pantalla nueva es otro panel y una tarjeta en `VentanaPrincipal`.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué FlatLaf y no JavaFX?
**Respuesta breve:** El cambio es de presentación y debe quedar en `view`; JavaFX obligaría a reescribir toda la vista y las pruebas sin aportar nada que el TP evalúe. Con FlatLaf el modelo y el controlador no se tocaron.

---

## DD-015: Cronómetro por nivel con Observer

- **Fecha:** 2026-11-04
- **Estado:** Propuesta
- **Patrón / principio:** Observer (GoF); OCP; Low Coupling. Funcionalidad adicional extra
- **Requisito relacionado:** Ninguno obligatorio; refuerza el resumen de desempeño por nivel

### 1. Problema y contexto
Se quiere medir cuánto tarda el jugador en resolver cada nivel y mostrarlo en el resumen, sin que el modelo sepa nada del tiempo. Lo que cambia es qué se mide y dónde se muestra; lo estable es el flujo de eventos de `ObservadorDeJuego`.

Reglas adoptadas: el tiempo arranca con el primer movimiento del nivel, se detiene en `nivelCompletado` y vuelve a cero en `nivelCambiado` (avanzar de nivel o cargar una partida).

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `Cronometro` como `ObservadorDeJuego` | El modelo no cambia; se registra en `Main` como `Logros` y `SonidosDelJuego`; reloj inyectable para probar | Repite Observer | (elegida) |
| Campo de tiempo dentro de `Nivel` o `Partida` | Sin clases nuevas | Mezcla reglas del juego con medición; el modelo pasa a depender del reloj del sistema | Viola SRP |
| Que la vista mida el tiempo con un `Timer` | Simple | La lógica de medición queda en `view`, sin pruebas y atada a Swing | Rompe MVC |

### 3. Decisión
`Cronometro` implementa `ObservadorDeJuego` y usa los eventos que el modelo ya emite. El reloj es un `LongSupplier` inyectado, así que las pruebas controlan el tiempo sin dormir hilos. `VentanaPrincipal` lo consulta para mostrar el tiempo en la pantalla de nivel completado.

### 4. Principios que favorece
- **OCP:** una función nueva sin modificar `Nivel`, `Partida` ni `DifusorDeEventos`.
- **Low Coupling:** el modelo no conoce el cronómetro.
- **SRP:** medir el tiempo es una responsabilidad aparte.

### 5. Consecuencias y costos
Es el tercer uso de Observer: es coherente pero repite el patrón, y la variedad la aportan otras decisiones. `reiniciarNivel` no reinicia el tiempo, porque emite `estadoRestaurado`, el mismo evento que deshacer, y el cronómetro no puede distinguirlos. El tiempo no entra en el puntaje ni en el archivo de guardado. El HUD todavía no lo muestra en vivo (haría falta un `Timer` de Swing).

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Observer (interfaz) | `ObservadorDeJuego` | Implementada |
| Observer concreto | `Cronometro` | Implementada |
| Subject | `DifusorDeEventos` | Implementada |
| Vista del dato | `PantallaNivelCompletado` | Implementada |

### 7. Cómo se extiende
Un logro "Completá un nivel en menos de 30 s" necesita pasar el tiempo a `EstadisticasDeJuego`. Mostrarlo en vivo en el HUD agrega un `Timer` en `view` que repinte y consulte `Cronometro.comoTexto()`.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué un Observer para el cronómetro y no un campo de tiempo en `Nivel`?
**Respuesta breve:** El modelo ya avisa cuándo se mueve el jugador y cuándo se completa un nivel, así que el cronómetro solo escucha esos eventos. Si el tiempo viviera en `Nivel`, las reglas del juego dependerían del reloj. Reconocemos que repite Observer: lo elegimos por su bajo costo, no por variedad.
---

## DD-016: Campaña por mundos con Composite

- **Fecha:** 2026-11-13
- **Estado:** Propuesta
- **Patrón / principio:** Composite (GoF); OCP; Information Expert. Funcionalidad adicional extra
- **Requisito relacionado:** Ninguno obligatorio; organiza los niveles del catálogo

### 1. Problema y contexto
Los niveles eran una lista plana y el catálogo ("tutorial = los primeros cuatro") dependía de un `i < 4` escrito en la vista, con una grilla fija de seis tarjetas que no alcanzaba para los ocho niveles. Se quiere agrupar los niveles en mundos, y poder tratar igual un nivel suelto, un mundo y la campaña entera. Lo que cambia es cómo se agrupan los niveles; lo estable es que `Partida` juega una lista de niveles en orden.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `ElementoDeCampania` con hoja (`NivelDeCampania`) y compuesto (`Mundo`) | El árbol se recorre y se aplana con la misma interfaz; agregar un mundo no toca el resto | Más clases; con dos niveles de profundidad podría alcanzar una lista | (elegida) |
| `Map<String, List<Nivel>>` de mundos | Poco código | El orden y el anidado quedan implícitos y la vista decide qué es un mundo | Mueve la estructura a la vista |
| Un campo "mundo" en cada nivel | Sin clases nuevas | Mezcla datos de presentación con el modelo del nivel | Viola SRP |

### 3. Decisión
`ElementoDeCampania` define `titulo()`, `detalle()`, `niveles()` y `cantidadDeNiveles()`. `NivelDeCampania` (hoja) envuelve un `Nivel`; `Mundo` (compuesto) guarda hijos y devuelve los niveles de todos ellos en orden. `CampaniaEstandar` arma la campaña: "Tutorial" (niveles 1 a 4) y "Desafíos" (5 a 8). `Main` le pasa `campania.niveles()` a `Partida` y una segunda carga, sin jugar, a `PantallaDeNiveles`, que recorre el árbol y muestra una sección por mundo dentro de un `JScrollPane`.

### 4. Principios que favorece
- **OCP:** un mundo nuevo es un `Mundo` más en `CampaniaEstandar`; ni `Partida` ni la vista cambian.
- **Information Expert:** cada elemento sabe cuántos niveles tiene.
- **SRP:** `Partida` no sabe de mundos.

### 5. Consecuencias y costos
La vista pregunta `instanceof Mundo` para decidir cómo dibujar (un método en el compuesto sería más puro, pero metería Swing en el modelo). La campaña se carga dos veces (una para jugar, otra para el catálogo) porque los niveles son mutables. Hoy los mundos no tienen puntaje agregado ni progreso: la operación compartida real es listar y contar niveles, lo que hace débil la justificación si no se agregan operaciones.

### 6. Dónde está en el código
| Rol en el patrón | Clase/Interfaz | Estado |
|---|---|---|
| Component | `ElementoDeCampania` | Implementada |
| Leaf | `NivelDeCampania` | Implementada |
| Composite | `Mundo` | Implementada |
| Armado | `CampaniaEstandar` (Pure Fabrication) | Implementada |
| Cliente | `PantallaDeNiveles`, `Main` | Implementada |

### 7. Cómo se extiende
Un tercer mundo es otro `Mundo` en `CampaniaEstandar` con sus archivos `nivelN.txt`. Un puntaje por mundo sería un método `puntaje()` en la interfaz que cada hoja responde y cada mundo suma.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Composite y no una lista de listas?
**Respuesta breve:** Porque un nivel, un mundo y la campaña se usan igual (listar sus niveles y contarlos) y los mundos pueden anidarse; con listas anidadas la vista tendría que conocer la profundidad. Reconocemos que hoy la operación compartida es chica y que el valor crece si se suman operaciones como el puntaje por mundo.

---

## DD-017: Modos de juego con Template Method

- **Fecha:** 2026-11-13
- **Estado:** Propuesta
- **Patrón / principio:** Template Method (GoF); OCP; DIP. Funcionalidad adicional extra
- **Requisito relacionado:** Ninguno obligatorio

### 1. Problema y contexto
`Partida.mover()` solo conocía una condición de fin (el nivel resuelto); no existía la derrota. Se quieren modos que cambien cuándo se pierde un nivel (por tiempo o por cantidad de movimientos) y, en uno, el puntaje. Lo estable es el orden en que se decide qué pasó después de un movimiento; lo que cambia es el límite.

### 2. Alternativas consideradas
| Alternativa | Ventajas | Desventajas | Por qué se descartó |
|---|---|---|---|
| `ModoDeJuego` abstracto con un método plantilla `evaluar` y ganchos | El orden (completar antes que perder) queda fijo para todos los modos; los ganchos de puntaje y de etiqueta tienen valor por defecto | Herencia: cada modo es una subclase | (elegida) |
| Strategy `CondicionDeDerrota` | Composición en vez de herencia; ya hay Strategy en el proyecto | El orden "completar gana" quedaría repetido en `Partida` y no cubre los ganchos de puntaje | Es una alternativa válida; Template Method se eligió por el esqueleto fijo |
| `if (modo == ...)` dentro de `Partida` | Poco código | Cada modo nuevo modifica `Partida` | Viola OCP |

### 3. Decisión
`ModoDeJuego.evaluar(Nivel)` es `final`: devuelve `NIVEL_COMPLETADO` si el nivel está resuelto; si no, `NIVEL_FALLIDO` si `limiteAgotado` (paso abstracto); si no, `JUGANDO`. Los ganchos `ajustarPuntaje` y `etiquetaDeMovimientos` tienen implementación por defecto. `ModoClasico` nunca pierde; `ModoMovimientosLimitados` pierde al llegar al máximo (200 por defecto); `ModoContrarreloj` usa el `Cronometro` y pierde pasados 180 segundos, y suma un punto por segundo que sobra. `Partida` recibe el modo (por defecto, clásico), agrega el estado `NIVEL_FALLIDO` y el evento `nivelFallido`; reintentar un nivel fallido lo reinicia y avisa `nivelCambiado`. `ModoContrarreloj` está en el paquete `tiempo` para que `model` no dependa del cronómetro. `GuardadoDePartida` anota una línea `MODO <nombre>`; un guardado sin esa línea se carga con el modo activo.

### 4. Principios que favorece
- **OCP:** un modo nuevo es una subclase; `Partida` no cambia.
- **DIP:** `Partida` depende de la abstracción `ModoDeJuego`, no de un reloj ni de un contador.
- **Information Expert:** cada modo sabe cuándo se agota su límite.

### 5. Consecuencias y costos
Contrarreloj solo revisa el tiempo al mover: si el jugador se queda quieto no pierde hasta su próxima jugada, y el HUD no muestra el reloj en vivo. Los topes (200 movimientos, 180 segundos) son provisorios: el cálculo con una búsqueda exhaustiva dio 19, 100 y 85 movimientos mínimos para los niveles 5, 6 y 7, y no se halló solución del nivel 8 con el límite de memoria usado, así que no se pudo calibrar con datos completos. Deshacer devuelve movimientos al contador (consistente con `EstadisticasDeNivel`). Al cargar una partida con reloj, se reproduce sin tiempo y no se puede perder por tiempo en la repetición.

### 6. Dónde está en el código
| Rol en el patrón | Clase | Estado |
|---|---|---|
| Clase abstracta con el método plantilla | `ModoDeJuego` | Implementada |
| Subclase concreta | `ModoClasico` | Implementada |
| Subclase concreta | `ModoMovimientosLimitados` | Implementada |
| Subclase concreta | `ModoContrarreloj` | Implementada |
| Contexto | `Partida` | Implementada |
| Selección | `PantallaDeModos`, `ControladorDeJuego.elegirModo` | Implementada |
| Derrota | `PantallaNivelFallido` | Implementada |

### 7. Cómo se extiende
Un modo "sin deshacer" es una subclase con `limiteAgotado` falso y un deshacer deshabilitado; un modo "una vida" fallaría al romperse una caja. Se agrega a la lista de `Main`.

### 8. Pregunta de defensa
**Pregunta:** ¿Por qué Template Method y no Strategy para los modos?
**Respuesta breve:** Porque hay un esqueleto que no puede cambiar (completar el nivel siempre gana a perderlo) y pasos que sí (el límite, el puntaje, el texto del contador). Template Method fija el esqueleto en un lugar. Reconocemos que con solo la condición de derrota alcanzaría un Strategy.

---

## Decisiones del equipo sobre reglas ambiguas (2026-10-28)
- **Deslizamiento:** una caja que se desliza sobre terreno resbaladizo se frena al llegar a un destino o a un cerrojo. Ya coincide con el código: `DESTINO` y `CERROJO` usan `MovimientoNormal` (DD-006).
- **Muros:** si la caja llave sale del cerrojo, sus muros **se vuelven a cerrar**. Reemplaza la regla provisoria de DD-007 ("queda abierto"). **Pendiente de implementar.**
- **Caja frágil rota:** el nivel se pierde o no según queden cajas suficientes para cubrir todas las bahías (destinos). Interpretación a confirmar por el equipo; hoy el cargador exige igual cantidad de cajas y destinos, así que cualquier rotura deja el nivel irresoluble. **Pendiente de confirmar e implementar.**

## Ambigüedades abiertas (a confirmar con el enunciado vigente / docente)
- Uso de Swing como biblioteca gráfica.
- ¿Qué es "un movimiento": cada paso del jugador o solo los empujes?
- Reglas del undo (cuántos pasos, usos consecutivos, qué restaura).
- Reglas del terreno resbaladizo, cajas frágiles y llave/cerrojo/muro.
- Formato del nivel `.txt` y cantidad de niveles (hoy: 4 niveles de ejemplo, uno por característica).
- Origen de los assets: hoy son generados por código (CC0); confirmar si el enunciado exige recursos de terceros de dominio público.
- Las dos funcionalidades adicionales (hoy: guardar/cargar con Command y logros con Observer + Strategy; propuesta a validar por el equipo y a confirmar con el enunciado).
- Criterio de puntaje (hoy: 1000 base, −10 por movimiento, −5 por empuje, −50 por deshacer; valores a ajustar).
- Qué pasa al terminar el último nivel (hoy: pasa por `NIVEL_COMPLETADO` y luego `JUEGO_TERMINADO`).
- Si las cajas llave y frágiles también deben terminar sobre destinos (hoy: todas las cajas).
- Resistencia de la caja frágil: fija en 3 (¿o definida por nivel?) y qué pasa si el nivel queda irresoluble.
- Si un muro se vuelve a cerrar cuando la llave sale del cerrojo (hoy: queda abierto) y cómo se asocian llaves, cerrojos y muros (hoy: id 1–9).

---

## Registro de uso de IA

| Entrada | Qué se hizo con apoyo de IA | Qué hizo y validó el equipo |
|---|---|---|
| DD-001 | Borrador de la entrada, estructura de paquetes y `pom.xml` | completar |
| DD-002 | Borrador de la entrada, código del modelo base y tests | completar |
| DD-003 | Borrador de la entrada, código de cajas, `Nivel` y tests | completar |
| DD-004 | Borrador de la entrada, fábrica, cargador, nivel de ejemplo y tests; el 2026-11-14 pasó a Factory Method con `CreadorDeCaja` y tests | completar |
| DD-005 | Evaluación State vs. enum, `Partida`, `Nivel.estaResuelto()` y tests | completar |
| DD-006 | Borrador de la entrada, estrategias de movimiento, hielo y tests | completar |
| DD-007 | Borrador de la entrada, cajas frágil/llave, Observer cerrojo–muro, cargador y tests | completar |
| DD-008 | Borrador de la entrada, Memento, historial, sesión de nivel y tests | completar |
| DD-009 | Borrador de la entrada, eventos del modelo, vista Swing, HUD, sonido, controlador y tests | completar |
| DD-010 | Borrador de la entrada (versión Flyweight y reescritura como Facade), repositorio de imágenes, `RecursosVisuales`, generador de assets y tests | completar |
| DD-011 | Borrador de la entrada, estrategia de puntaje, resumen de nivel y tests | completar |
| DD-012 | Propuesta de las dos funcionalidades adicionales, borrador de la entrada, acciones, guardado y tests | completar |
| DD-013 | Borrador de la entrada, logros, criterios y tests | completar |
| DD-014 | Plan y borrador de la entrada, tema FlatLaf, HUD dibujado, pantallas, destello, repositorio de fuentes y tests de vista | completar |
| DD-015 | Borrador de la entrada, `Cronometro` y tests | completar |
| DD-016 | Borrador de la entrada, `Composite` de campaña, pantalla de niveles y tests | completar |
| DD-017 | Borrador de la entrada, modos de juego, estado fallido, pantallas, guardado con modo y tests | completar |
