> Procedencia: material del paquete sokoban-design-guard.skill. Los requisitos, datos institucionales, exclusiones de patrones y ejemplos corresponden a su contexto original; confirmar su vigencia para este proyecto. Las instrucciones del usuario y SKILL.md prevalecen. Los ejemplos no acreditan implementación ni validación del equipo.

# Patrones GoF aplicados al Sokoban

Decisión del equipo: **no usar Proxy ni Adapter** (proyecto desde cero, nada que adaptar; Proxy casi no aplica). No recomendarlos.

Regla de oro: un patrón se justifica por un **problema real que resuelve**, no por aparecer en la materia. Para cada candidato hay que poder responder: *¿qué cambia con frecuencia?, ¿qué pasaría sin el patrón?, ¿qué costo agrega?*

## Contenido
1. Mapa rápido: problema del juego → patrón candidato
2. Detalle por problema (cuándo sí, cuándo no, combinaciones)
3. Patrones sin uso natural en este proyecto
4. Candidatos para las dos funcionalidades adicionales
5. Combinaciones frecuentes y trampas

---

## 1. Mapa rápido

| Problema del juego | Candidato principal | Alternativas |
|---|---|---|
| Comportamiento distinto por tipo de caja (normal, frágil, llave) | Polimorfismo (jerarquía o interfaz) + **Strategy** | State (frágil), Decorator |
| Resistencia de la caja frágil que cambia al empujar | **State** | atributo simple + polimorfismo |
| Terreno resbaladizo vs. normal (cómo se mueve una caja encima) | **Strategy** | polimorfismo en `Terreno` |
| Crear cajas y terrenos a partir de caracteres del `.txt` | **Factory Method** / Simple Factory | Abstract Factory, Builder |
| Armar un nivel completo desde un archivo (varias partes, validación) | **Builder** | Factory Method |
| Undo (restaurar estado) | **Memento** | Command (con undo), snapshot simple |
| Registrar acciones del jugador (movimientos, empujes, historial) | **Command** | Observer |
| HUD, sonidos y vista se actualizan cuando cambia el modelo | **Observer** | eventos propios |
| Llave sobre cerrojo abre muros | **Observer** | Mediator |
| Cálculo del puntaje con criterio intercambiable | **Strategy** | Template Method |
| Pasos comunes al cargar/validar/iniciar nivel | **Template Method** | Strategy |
| Estados del juego (menú, jugando, nivel completado, fin) | **State** | flags (evitar) |
| Un único punto de acceso al audio o a la configuración | **Singleton** (con cautela) | inyección de dependencias |
| Recorrer las casillas o niveles sin exponer la estructura | **Iterator** | `Iterable` de Java |
| Reutilizar imágenes/sonidos cargados una sola vez | **Flyweight** | caché simple |
| Tablero como estructura compuesta (casillas con contenido) | **Composite** (si hay jerarquía real) | colecciones simples |
| Simplificar la interfaz entre controlador y subsistemas | **Facade** | Controller GRASP |
| Combinar comportamientos opcionales (efectos, bonificaciones) | **Decorator** | Strategy |
| Estructuras de varios objetos que cambian juntas (temas gráficos) | **Abstract Factory** | Builder |

## 2. Detalle por problema

### Tipos de caja (normal / frágil / llave)
- **Problema:** las tres comparten reglas de movimiento; difieren en lo que ocurre al empujarlas o al colocarlas.
- **Opción A: jerarquía polimórfica** (`Caja` abstracta o interfaz y subclases): simple y legible; suficiente si el comportamiento extra es pequeño.
- **Opción B: Strategy** (la caja delega "qué pasa al ser empujada" o "qué pasa al llegar a una casilla" en un colaborador): más flexible, más clases.
- **Cuándo no:** si solo hay un `switch` pequeño y estable, no hace falta nada elaborado; pero el enunciado ya pide tres tipos y la extensibilidad es parte de lo que se evalúa, así que el polimorfismo suele estar justificado.
- **Trampa:** herencia profunda o subclases que rompen el contrato de `Caja` (LSP).

### Frágil con resistencia (State)
- Aplica si la caja cambia de comportamiento según su resistencia (intacta, dañada, rota). Si es solo un contador que decrementa, un atributo alcanza; **no forzar State** para un entero.
- State se justifica si cada estado tiene comportamiento y reglas de transición propios (por ejemplo, imagen y sonido distintos, rotura con efecto).

### Terreno resbaladizo (Strategy / polimorfismo)
- Encapsular "cómo se mueve una caja sobre este terreno" (`Deslizamiento`, `MovimientoNormal`) evita condicionales `if (terreno.esResbaladizo())` repartidos.
- Cuidado con la regla exacta de parada (ver ambigüedades en `requisitos.md`).

### Creación desde `.txt` (Factory)
- **Factory Method / Simple Factory:** mapear carácter → objeto (`'#'` → pared). Sencillo y útil. Evita `switch` desparramados.
- **Builder:** si el nivel se construye en varios pasos (dimensiones, elementos, validaciones) o hay muchas variantes. No usar Builder para objetos de dos campos.
- **Abstract Factory:** solo si hay familias (por ejemplo, temas gráficos).

### Undo (Memento vs. Command)
- **Memento:** guarda snapshots del estado (posiciones, resistencias, estado de muros, contadores). Muy natural aquí porque el enunciado dice "restaurar el estado del nivel 5 movimientos hacia atrás".
- **Command con undo:** cada movimiento es un objeto con `ejecutar()` y `deshacer()`; más fino pero cada comando debe saber revertirse (más fácil de equivocarse con deslizamientos, roturas y muros).
- **Recomendación habitual:** Memento, con un historial acotado a 15 estados. Combinar con Command si además se quiere replay o registro de acciones.
- **Trampa:** copias superficiales (el snapshot comparte referencias con el estado vivo). Definir qué entra en el memento: jugador, cajas con resistencia, muros, contadores.

### Notificación a vista, HUD y sonido (Observer)
- El modelo emite eventos (`caja movida`, `caja rota`, `muro abierto`, `nivel completado`) y los observadores reaccionan (vista, HUD, audio).
- Valida MVC: el modelo no conoce a la vista.
- **Trampa:** eventos demasiado genéricos ("cambió algo") que obligan a la vista a recalcular todo sin criterio; o usar Observer con un único observador fijo sin variación prevista (justificar de otro modo).

### Puntaje (Strategy)
- El criterio es libre y probablemente cambie mientras se prueba: aislarlo detrás de `EstrategiaDePuntaje` es un caso de Protected Variations bien justificable.
- **Cuándo no:** si el equipo nunca va a cambiar la fórmula, una clase `CalculadorDePuntaje` simple es suficiente; decidir con honestidad.

### Estados de la aplicación (State)
- Menú, jugando, nivel completado, juego terminado: un State claro evita banderas booleanas combinadas.

### Singleton (con cautela)
- Solo si hay un problema real de instancia única compartida (por ejemplo, el reproductor de sonido). Documentar por qué no se inyecta. Muchos profesores lo consideran un antipatrón por el estado global; si se usa, justificarlo y limitarlo.

### Flyweight
- Cache de imágenes por tipo de casilla: muchas casillas comparten la misma imagen. Es un caso legítimo y fácil de defender si se mide el beneficio o se explica el problema (cargar la misma imagen cientos de veces).

### Iterator
- En Java ya existe `Iterable`; úselo cuando se expone una colección de casillas o niveles sin revelar su estructura interna. No reimplementar el patrón a mano si no hace falta.

## 3. Patrones sin uso natural en este proyecto

- **Proxy y Adapter:** excluidos por decisión del equipo.
- **Bridge, Chain of Responsibility, Interpreter, Mediator, Visitor:** pueden aparecer en funcionalidades adicionales, pero rara vez son necesarios en el núcleo. No recomendarlos sin un problema concreto.
  - *Chain of Responsibility* podría validar un movimiento en cadena (¿hay pared?, ¿hay caja?, ¿hay muro cerrado?), pero es discutible: una validación directa suele ser más clara.
  - *Mediator* podría coordinar llaves, cerrojos y muros, aunque Observer suele alcanzar.
  - *Visitor* sirve si hay muchas operaciones sobre la jerarquía de elementos (render, serialización, validación); no suele hacer falta.
- **Prototype:** clonar estados para el undo; suele ser un detalle de implementación del Memento.

## 4. Candidatos para las dos funcionalidades adicionales

El enunciado exige **dos** que impliquen patrones. Proponer solo las que se apoyen en un patrón con problema real, y evitar que parezcan un parche. Cada una con patrón, valor y costo:

| Funcionalidad | Patrón(es) | Valor para la evaluación | Costo / riesgo |
|---|---|---|---|
| **Replay / reproducción de la partida** | Command (+ Memento) | Muy defendible: historial de acciones reproducible | Medio; exige acciones determinísticas |
| **Temas gráficos (skins)** | Abstract Factory + Flyweight | Evidencia extensibilidad y reutilización de assets | Medio; requiere más assets de dominio público |
| **Sistema de pistas / solver** | Strategy (algoritmos BFS/A*) | Intercambiar algoritmos; fácil de justificar | Alto si el solver es complejo |
| **Logros / estadísticas** | Observer + Strategy | Desacopla del modelo; bajo costo | Bajo/medio; persistencia aparte |
| **Guardado y carga de partida** | Memento | Reutiliza el Memento del undo | Medio; serialización |
| **Editor de niveles** | Builder + Command | Creativo y llamativo | Alto |
| **Modos de dificultad (límite de movimientos, tiempo)** | Decorator o Strategy | Reglas combinables sin tocar el núcleo | Bajo/medio |
| **Puntajes/ranking persistente** | Repository (no GoF) + Strategy | Útil, pero patrón menos "GoF" | Bajo |

Al ayudar a elegir, preferir dos funcionalidades que **reutilicen decisiones ya tomadas** (por ejemplo, guardar partida reutiliza el Memento) y que no dupliquen el mismo patrón sin necesidad: la variedad de patrones bien aplicados se valora, pero sin forzarla.

## 5. Combinaciones frecuentes y trampas

- **Memento + Command:** Command para registrar acciones, Memento para restaurar estados.
- **Observer + MVC:** el modelo es el sujeto, la vista el observador.
- **Factory + Builder:** la fábrica interpreta caracteres; el builder arma el nivel completo.
- **Strategy + State:** se parecen; la diferencia es la intención: Strategy elige un algoritmo desde fuera, State cambia el comportamiento según el estado interno y gestiona sus propias transiciones. Si la elección viene del contexto (terreno, puntaje), es Strategy; si el objeto cambia solo (caja frágil), es State.
- **Trampas generales:** un patrón por cada problema pequeño (sobreingeniería); patrones nombrados en el informe pero ausentes en el código; clases llamadas `XxxFactory` o `XxxObserver` que no cumplen la estructura real del patrón. El profesor evalúa la *correcta* aplicación: las estructuras y los roles deben coincidir con el patrón.
