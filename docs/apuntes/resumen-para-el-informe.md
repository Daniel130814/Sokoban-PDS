# Resumen para el informe técnico y la defensa

Material de apoyo, no el informe final. Cada fila remite a la entrada del
[registro de decisiones](decisiones-de-diseno.md) donde está el razonamiento completo (alternativas, costos,
pregunta de defensa). Todas las decisiones siguen en estado **Propuesta**: ninguna fue validada todavía por el
equipo, y los requisitos académicos usados provienen del paquete de la skill y están **sin confirmar** con el
enunciado vigente.

## 1. Patrones y principios: dónde están en el código

| Decisión | Patrón / principio | Problema que resuelve | Clases reales | Prueba que lo respalda |
|---|---|---|---|---|
| DD-001 | MVC, SRP | Separar reglas, vista y entrada | paquetes `model`, `view`, `controller` | `ArquitecturaTest` (el modelo no importa Swing, AWT ni javax.sound) |
| DD-002 | SRP, Information Expert | Responsabilidades chicas en el modelo base | `Posicion`, `Tablero`, `Jugador`, `Direccion`, `TipoCelda` | `TableroTest` |
| DD-003 | Polimorfismo, OCP, LSP, GRASP Controller | Empujar cajas sin `switch` por tipo | `Caja`, `CajaNormal`, `Nivel` | `NivelTest` (incluye una caja anónima) |
| DD-004 | Simple Factory (no es GoF) | Traducir caracteres del `.txt` a objetos | `FabricaDeElementos`, `CargadorDeNivel` | `CargadorDeNivelTest` |
| DD-005 | Sin patrón (State descartado) | Estados de la partida | `EstadoPartida`, `Partida` | `PartidaTest` |
| DD-006 | **Strategy** | Cómo se mueve una caja según el terreno | `ReglaDeMovimiento`, `MovimientoNormal`, `Deslizamiento`, `TipoCelda` | `DeslizamientoTest` |
| DD-007 | **Observer** (cerrojo → muros) | Una llave abre muros sin acoplarlos | `Cerrojo`, `ObservadorDeCerrojo`, `Muro`, `CajaLlave`, `CajaFragil` | `LlaveYMuroTest`, `CajaFragilTest` |
| DD-008 | **Memento** | Deshacer y reiniciar con estado completo | `Nivel`, `EstadoNivel`, `EstadoCaja`, `HistorialDeEstados`, `SesionDeNivel` | `EstadoNivelTest`, `HistorialDeEstadosTest`, `SesionDeNivelTest` |
| DD-009 | **Observer**, MVC, GRASP Controller | Vista, HUD y sonido reaccionan sin acoplarse al modelo | `ObservadorDeJuego`, `DifusorDeEventos`, `PanelDeTablero`, `PanelHud`, `SonidosDelJuego`, `ControladorDeJuego` | `EventosDeJuegoTest`, `SonidosDelJuegoTest`, `ControladorDeJuegoTest` |
| DD-010 | **Flyweight** | Compartir imágenes entre celdas | `RepositorioDeImagenes` | `VistaTest` |
| DD-011 | **Strategy** | Criterio de puntaje intercambiable | `EstrategiaDePuntaje`, `PuntajePorEficiencia`, `Partida` | `PuntajeTest` |
| DD-012 | **Command** (funcionalidad adicional 1) | Guardar, cargar y reproducir la partida | `Accion`, `Mover`, `Deshacer`, `Reiniciar`, `Continuar`, `GuardadoDePartida` | `GuardadoYCargaTest`, `AccionTest` |
| DD-013 | **Observer + Strategy** (funcionalidad adicional 2) | Logros sin tocar el modelo | `Logros`, `CriterioDeLogro`, `Logro`, `CatalogoDeLogros` | `LogrosTest` |

Patrones GoF efectivamente aplicados: Strategy (2 usos y logros), Observer (3 usos), Memento, Flyweight, Command.
Proxy y Adapter no se usaron (preferencia provisional de la skill). Singleton se evaluó y se descartó (DD-009).

### Puntos débiles que conviene conocer antes de la defensa
- `Nivel` tiene 16 métodos y reúne jugada, Memento y eventos (el escáner lo marca): es el candidato a dividir.
- Strategy y Observer se repiten; hay que poder explicar por qué cada uso es genuino (DD-006, DD-009, DD-011, DD-013).
- Varias decisiones reconocen que el patrón tiene hoy una sola implementación (DD-003, DD-011) o que es un
  Strategy funcional con lambdas (DD-013).
- State se descartó dos veces (DD-005, DD-007) por no tener comportamiento propio por estado.

## 2. Cobertura de los requisitos (según `requisitos.md`, sin confirmar)

| Requisito | Estado | Detalle |
|---|---|---|
| Elementos del tablero | Cubierto | Paredes, pisos, destinos, hielo, cerrojos, muros abiertos y cerrados, tres tipos de caja y jugador |
| Niveles desde `.txt` | Cubierto | 4 niveles de ejemplo, uno por archivo; son demostrativos y chicos |
| Movimiento con flechas, solo empujar, una caja | Cubierto | Probado en `NivelTest` |
| Deshacer: 15 estados, 5 pasos, 3 usos consecutivos | Cubierto con reglas provisorias | Ver DD-008 |
| Condición de victoria y avance de nivel | Cubierto | Pantalla final: hoy un diálogo |
| Puntaje y resumen por nivel | Cubierto | Fórmula provisoria (DD-011) |
| HUD con movimientos, empujes, nivel y botones | Cubierto | `PanelHud` |
| Imágenes y efectos de sonido | Cubierto con una salvedad | Generados por código (CC0); falta confirmar si se exigen recursos externos |
| Dos funcionalidades adicionales con patrones | Cubierto como propuesta | DD-012 y DD-013, a validar por el equipo |
| Java con Swing | Cubierto | Pendiente confirmar Swing con el enunciado |
| Repositorio con README de ejecución | Cubierto | Falta verificar que el repositorio sea público |
| Video de gameplay | **Pendiente** | Lo hace el equipo |
| Diagrama de clases en StarUML | **Pendiente** | Hay una base en `docs/uml/diagrama-de-clases.md` |
| Informe técnico | **Pendiente** | Este resumen y el registro de decisiones son la materia prima |
| Declaración de uso de IA | **Pendiente de completar** | Ver sección 3 |
| Entrega por correo institucional | **Pendiente** | La hace el equipo |

Qué no se verificó: el juego no se probó jugando a mano (ni sonidos, ni diálogos, ni botones Guardar y Cargar);
las pruebas automáticas cubren el modelo, el controlador y el dibujo del tablero sobre una imagen.

## 3. Declaración de uso de IA (borrador)

Un asistente de IA (Claude, de Anthropic, usado desde Claude Code) propuso el diseño por etapas, escribió el código
fuente, las pruebas, el generador de assets y el registro de decisiones de este repositorio, siguiendo las
instrucciones del equipo en cada etapa. Lo que el equipo hizo y validó (elección final de patrones, revisión del
código, ajustes, pruebas manuales) **no consta en este repositorio**: la columna correspondiente del registro de
decisiones dice "completar" y debe llenarla el equipo con lo que realmente hizo. Esta declaración debe revisarse
antes de entregarla, y solo es una obligación académica si el enunciado vigente lo exige.
