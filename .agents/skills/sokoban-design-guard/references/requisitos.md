> Procedencia: material del paquete sokoban-design-guard.skill. Los requisitos, datos institucionales, exclusiones de patrones y ejemplos corresponden a su contexto original; confirmar su vigencia para este proyecto. Las instrucciones del usuario y SKILL.md prevalecen. Los ejemplos no acreditan implementación ni validación del equipo.

# Requisitos del TP Sokoban (checklist + ambigüedades)

Fuente: enunciado "Trabajo Integrador" de Proceso de Desarrollo de Software (Prof. Pepe, UADE). Usar este archivo para verificar cobertura al revisar código y para detectar decisiones que el equipo debe tomar y documentar.

## Contenido
1. Requisitos funcionales
2. Requisitos técnicos y entregables
3. Ambigüedades a resolver (decisiones del equipo)
4. Qué se evalúa

---

## 1. Requisitos funcionales

### Tablero
Elementos: paredes, espacios vacíos, cajas normales, cajas frágiles, cajas llave, casillas de destino, terrenos resbaladizos, casilleros cerrojo, muros abiertos/cerrados, jugador.

### Niveles
- Se cargan desde archivos de texto `.txt` con la disposición inicial en caracteres.
- Varios niveles, cada uno en su archivo.

### Movimiento del jugador
- Teclas direccionales (flechas).
- Solo **empuja** cajas (no tira de ellas).
- No empuja más de una caja a la vez.
- No atraviesa cajas, paredes ni muros cerrados.

### Tipos de caja
- **Normal:** el jugador la empuja en su misma dirección. En terreno resbaladizo, se desliza hasta chocar con otro objeto o llegar a un espacio vacío. No atraviesa cajas, paredes ni muros cerrados.
- **Frágil:** igual que la normal, pero con **resistencia**: cada empuje la reduce; al llegar a cero, **se rompe**.
- **Llave:** igual que la normal, pero al colocarse sobre un **casillero cerrojo** abre los **muros** correspondientes.

### Deshacer movimientos
- Se permiten deshacer los **últimos 15 movimientos**.
- Botón *undo*: cada pulsación restaura el estado del nivel **5 movimientos hacia atrás**.
- Máximo **tres usos consecutivos**.

### Condición de victoria
- Todas las cajas en sus destinos, avanzando al siguiente nivel.

### Puntaje
- Al terminar cada nivel, mostrar resumen: movimientos, empujes, uso del undo y **puntaje final**.
- El criterio de puntaje lo define el equipo.

### Interfaz de usuario (HUD)
- Mostrar movimientos, empujes y nivel actual.
- Botones visibles para **deshacer** y **reiniciar nivel**.

### Gráficos y sonido
- Todos los elementos del tablero representados por **imágenes**.
- Acciones acompañadas de **efectos de sonido** apropiados.
- Todos los **assets de dominio público** (anotar la fuente de cada uno para el informe).

### Funcionalidades adicionales
- **Dos** funcionalidades adicionales obligatorias que impliquen aplicación de patrones de diseño.

## 2. Requisitos técnicos y entregables

- Java con Swing (GUI 2D).
- Repositorio público de GitHub con el código, más un `README` con instrucciones para ejecutar.
- Video de gameplay que muestre **todas** las funcionalidades.
- Diagrama de clases en **StarUML**.
- Informe técnico con decisiones de diseño y su justificación; **declarar el uso de IA**.
- Entrega por correo institucional (un integrante envía, con copia al resto, indicando nombre del equipo y apellido y nombre de cada integrante).

## 3. Ambigüedades a resolver

Cuando el código o la consulta toque alguno de estos puntos, pedir una decisión explícita y registrarla en `docs/apuntes/decisiones-de-diseno.md`. Para los marcados con (*), conviene confirmarlo con el docente.

**Undo**
- (*) ¿Qué es "un movimiento"? ¿Cada paso del jugador (con o sin empuje) o solo los empujes? Define el tamaño de la historia.
- Los últimos 15 movimientos son el tope de historia, y cada undo retrocede 5: ¿qué ocurre si hay menos de 5 movimientos guardados? (Opciones: retroceder lo que haya, o no permitirlo.)
- "Tres usos consecutivos": ¿consecutivos significa sin movimientos intermedios? ¿El contador se reinicia al mover el jugador? ¿Y al reiniciar el nivel?
- ¿El undo restaura también contadores (movimientos, empujes) y la resistencia de cajas frágiles? ¿Se puede "des-romper" una caja? Lo coherente es restaurar el estado completo.
- ¿El uso del undo penaliza el puntaje? (El resumen debe informarlo; la penalización es decisión del equipo.)

**Terreno resbaladizo**
- "Se desliza hasta chocar con otro objeto o llegar a un espacio vacío": ¿"espacio vacío" significa piso normal (no resbaladizo) donde la caja se detiene? Confirmar y documentar la regla exacta.
- ¿Una caja que se desliza y cae sobre un destino se detiene ahí? ¿Y sobre un casillero cerrojo?
- ¿Cuenta el deslizamiento como un único empuje? (Lo habitual: sí.)
- ¿El jugador también resbala? El enunciado solo habla de cajas; no agregar sin decidirlo.

**Cajas frágiles**
- ¿Resistencia inicial fija o definida por nivel (en el `.txt`)?
- ¿Qué pasa al romperse? ¿Desaparece sin más? ¿El nivel puede quedar irresoluble? Si sí, ¿se detecta y se ofrece reiniciar?
- ¿Una caja frágil que llega a un destino con resistencia 1 cuenta como completada?

**Llaves, cerrojos y muros**
- (*) ¿Cómo se asocian llaves/cerrojos con muros? ¿Por identificador (letra, color), uno a uno, uno a muchos?
- ¿Un muro se vuelve a cerrar si la caja llave sale del cerrojo? (Define si el estado de los muros es derivado o persistente.) Esto interactúa con el undo.
- ¿Puede una caja llave que no está sobre un cerrojo activarse con otro tipo de caja encima?

**Victoria y niveles**
- ¿Qué pasa al terminar el último nivel? ¿Pantalla final? ¿Vuelta al primero?
- ¿Las cajas llave y frágiles también deben terminar sobre destinos? (El enunciado dice "todas las cajas".)

**Puntaje**
- Fórmula libre. Documentar el criterio y por qué (ej.: penalizar movimientos, empujes y undos). Aislarlo para poder cambiarlo (candidato natural a Strategy).

**Formato de niveles**
- Libre. Definir la leyenda de caracteres (`#` pared, ` ` vacío, etc.), cómo se codifican resistencia, ids de llave/cerrojo/muro, y manejar archivos mal formados.

## 4. Qué se evalúa (criterios del enunciado)

1. Correcta aplicación de principios y patrones (SOLID, GRASP, MVC, GoF). **Foco principal.**
2. Creatividad.
3. Calidad del código.
4. Documentación.
5. Diseño general de la solución.
6. **Defensa exhaustiva** por cada integrante (oral y/o escrita), que determina la calificación final.
