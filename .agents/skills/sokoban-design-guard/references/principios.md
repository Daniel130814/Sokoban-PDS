> Procedencia: material del paquete sokoban-design-guard.skill. Los requisitos, datos institucionales, exclusiones de patrones y ejemplos corresponden a su contexto original; confirmar su vigencia para este proyecto. Las instrucciones del usuario y SKILL.md prevalecen. Los ejemplos no acreditan implementación ni validación del equipo.

# Principios de diseño: señales de alerta en el Sokoban (Java/Swing)

Usar este archivo al revisar código. Para cada principio: qué significa en este proyecto, señales de violación y la corrección típica. La idea es detectar problemas *reales*, no recitar teoría.

## Contenido
1. SOLID
2. GRASP
3. MVC en Swing
4. Otras buenas prácticas
5. Errores frecuentes en este TP

---

## 1. SOLID

### SRP: una clase, una razón para cambiar
- **Señales:** una clase `Juego`/`Tablero`/`GamePanel` que a la vez carga archivos, aplica reglas, dibuja, reproduce sonido y calcula puntaje; métodos de más de ~40 líneas; clases de más de ~300 líneas; nombres con "Manager", "Handler" o "Utils" que acumulan de todo.
- **Pregunta de prueba:** ¿cuántos motivos distintos habría para modificar esta clase? (cambio de reglas, de formato de nivel, de gráficos, de puntaje…). Más de uno es sospechoso.
- **Corrección:** separar por responsabilidad (cargador de niveles, reglas de movimiento, puntaje, render, audio).

### OCP: abierto a extensión, cerrado a modificación
- **Señales:** cadenas de `if/else` o `switch` sobre el *tipo* de caja o de terreno; `instanceof` repetido; agregar un tipo nuevo obliga a tocar varios archivos.
- **Prueba:** "¿Qué tengo que modificar para agregar una caja nueva (ej.: caja pesada)?". Si la respuesta es "varias clases existentes", viola OCP.
- **Corrección:** polimorfismo (cada tipo decide su comportamiento), Strategy, State, Factory para la creación.

### LSP: las subclases deben poder reemplazar a su base
- **Señales:** una subclase que lanza `UnsupportedOperationException`, ignora o invierte el contrato del padre; código cliente que pregunta "¿qué subclase sos?" antes de usarla; `CajaFragil` que rompe invariantes de `Caja` (por ejemplo, haciendo que `mover` a veces no mueva sin que el contrato lo permita).
- **Corrección:** revisar la jerarquía; preferir composición (comportamiento de empuje como colaborador) si la herencia fuerza excepciones.

### ISP: interfaces pequeñas y específicas
- **Señales:** una interfaz `Elemento` con métodos que la mayoría de las implementaciones dejan vacíos (`abrir()` en una pared, `romper()` en un jugador); listeners enormes.
- **Corrección:** dividir en roles (`Empujable`, `Fragil`, `Activable`).

### DIP: depender de abstracciones
- **Señales:** controlador o modelo con `new` de clases concretas por todos lados; campos declarados como `ArrayList<...>` en lugar de `List<...>`; el modelo conoce clases de la vista; reglas de alto nivel atadas a detalles (por ejemplo, la lógica de nivel dependiente de `javax.sound`).
- **Corrección:** inyectar dependencias por constructor, depender de interfaces, aislar creación en fábricas.

## 2. GRASP

- **Information Expert:** la responsabilidad va a la clase que tiene la información. Señal de violación: un controlador que pregunta de a una cosa por vez a la caja (`getResistencia()`, `setResistencia(...)`) y decide por ella; mejor `caja.recibirEmpuje()`.
- **Creator:** quién crea los objetos. ¿Quién instancia las cajas? Idealmente quien las agrega o contiene, o una fábrica si la creación es compleja (parseo de `.txt`).
- **Controller (de caso de uso):** una clase que recibe eventos de la UI y los delega al modelo. Señal de violación: el `KeyListener` implementa reglas del juego; o un "controlador" que es una clase Dios.
- **Low Coupling:** pocas dependencias entre clases. Señal: muchos imports cruzados, ciclos entre paquetes, cambios que se propagan en cascada.
- **High Cohesion:** los métodos de una clase se relacionan entre sí. Señal: métodos que no usan ningún campo de la clase.
- **Polymorphism:** variar comportamiento por tipo con polimorfismo en lugar de condicionales por tipo.
- **Pure Fabrication:** crear una clase artificial cuando ninguna del dominio es adecuada (ej.: `GestorDeHistorial`, `RepositorioDeNiveles`). Es válido si evita contaminar el dominio, pero justificarlo.
- **Indirection:** introducir un intermediario para desacoplar (ej.: el controlador entre vista y modelo).
- **Protected Variations:** encapsular los puntos que cambian detrás de una interfaz estable (ej.: formato de nivel, cálculo de puntaje, reproducción de sonido).

## 3. MVC en Swing

Estructura esperada:
- **Modelo:** estado y reglas del juego (tablero, jugador, cajas, terrenos, puntaje, historial). **Sin imports de `javax.swing` ni `java.awt`** (con la excepción discutible de tipos de valor muy simples; evitar de todos modos).
- **Vista:** componentes Swing que dibujan el modelo y exponen eventos. No decide reglas.
- **Controlador:** traduce eventos de la UI (flechas, botones) en llamadas al modelo.

**Señales de violación:**
- El modelo importa `JPanel`, `Graphics`, `ImageIcon`, `Color`.
- La vista modifica el estado del modelo directamente (por ejemplo, `tablero.getCasilla(x, y).setCaja(null)` desde `paintComponent`).
- La lógica de victoria o de empuje está en un `ActionListener`/`KeyListener`.
- La vista consulta al modelo mediante polling con un `Timer` en lugar de ser notificada de los cambios (candidato a Observer).
- Estado duplicado: la vista guarda su propia copia de posiciones.

**Detalles de Swing que suelen ignorarse:**
- Actualizar componentes desde el Event Dispatch Thread (`SwingUtilities.invokeLater`).
- Las teclas se manejan mejor con `Key Bindings` (`InputMap`/`ActionMap`) que con `KeyListener` si hay problemas de foco.
- No cargar imágenes ni sonidos en `paintComponent` (lectura repetida de disco).
- Reproducir sonidos fuera del hilo de la UI cuando bloquean.

## 4. Otras buenas prácticas

- Encapsulamiento: campos privados, sin getters que exponen colecciones mutables (devolver copias o vistas no modificables).
- Evitar números y cadenas mágicas: constantes o enums para direcciones, tipos de casilla y caracteres del `.txt`.
- Pruebas: aunque no sea requisito, un par de tests del modelo (JUnit) demuestran que el modelo es independiente de Swing y refuerzan la defensa.
- Manejo de errores: archivos de nivel faltantes o mal formados no deben cerrar el juego en silencio; no usar `catch` vacíos.
- Nombres consistentes y una única convención de idioma (castellano o inglés) en todo el código.
- `README` ejecutable y repo ordenado (paquetes por capa o por feature, sin archivos compilados en el repositorio).

## 5. Errores frecuentes en este TP

1. **Clase `Juego`/`Tablero` omnipotente** que mezcla modelo, reglas y dibujado.
2. **`instanceof`/`switch` por tipo de caja** en el controlador o en el tablero (viola OCP y Polymorphism).
3. **Undo implementado copiando mal el estado** (referencias compartidas en vez de copias profundas): el estado "restaurado" sigue mutando.
4. **Singletons por comodidad** (para audio, historial o configuración) sin justificación: oculta dependencias y dificulta probar. Si se usa, justificarlo con un problema real.
5. **Patrón por decoración:** una interfaz con una única implementación sin necesidad, una fábrica que solo hace `new`, un Observer con un solo observador fijo sin variación prevista.
6. **Lógica de nivel en la vista** (por ejemplo, detectar la victoria al pintar).
7. **Cargador de niveles acoplado a todo** (que además crea vistas o reproduce sonido).
8. **Diseño que no coincide con el diagrama de clases** de StarUML. El informe y el diagrama deben reflejar el código real.
