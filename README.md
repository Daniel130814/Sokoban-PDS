# Sokoban — Proceso de Desarrollo de Software

Proyecto académico de la materia **Proceso de Desarrollo de Software**. Desarrollaremos una versión del juego Sokoban en **Java**, incorporando nuevas funcionalidades y aplicando progresivamente Programación Orientada a Objetos, principios **SOLID**, patrones **GRASP** y patrones de diseño **GoF**, como Strategy.

El objetivo es construir una solución clara, mantenible y extensible, y fundamentar las decisiones de diseño a medida que avance la cursada.

## Equipo

Grupo **Los Desinformaticos**.

### Integrantes

- Tomás Bruno (LU 1199741)
- Daniel Vidal (LU 1199924)
- Francisco Druetta (LU 1175550)
- Tomás Jarolaski (LU 1217710)
- Nehuen Zaloaga (LU 1201698)

## Cómo ejecutar

Requisitos: JDK 21. No hace falta instalar Maven (se usa el Maven Wrapper incluido).

```bash
./mvnw test
```

Ejecuta los tests. En Windows se usa `mvnw.cmd`.

```bash
./mvnw compile
java -cp target/classes sokoban.Main
```

Compila y abre el juego.

## Cómo se juega

- **Flechas del teclado:** mover al jugador. Solo se empuja (no se tira) y una caja a la vez.
- **Deshacer:** retrocede hasta 5 movimientos; se puede usar 3 veces seguidas y guarda los últimos 15 estados.
- **Reiniciar:** vuelve al comienzo del nivel.
- **Guardar / Cargar:** guarda la partida en `partida-guardada.txt` (en el directorio donde se ejecuta) y la retoma.
- **Elementos:** cajas normales, frágiles (se rompen tras 3 empujes), cajas llave que abren muros al quedar sobre su cerrojo, hielo en el que las cajas se deslizan, y un puntaje por nivel.
- **Logros:** el HUD muestra los logros desbloqueados en la sesión.

Los niveles son archivos de texto en `src/main/resources/levels`. Leyenda: `#` pared, ` ` piso, `.` destino,
`$` caja, `*` caja sobre destino, `%` caja frágil, `@` jugador, `+` jugador sobre destino, `~` hielo,
`a`–`i` caja llave, `1`–`9` cerrojo y `A`–`I` muro cerrado (la letra y el número se corresponden: `a`, `1` y `A` van juntos).

## Estructura del código

| Paquete | Rol |
|---|---|
| `sokoban.model` | Reglas del juego, sin ninguna dependencia de Swing (subpaquetes `carga`, `enums`, `puntaje`) |
| `sokoban.controller` | Traduce teclas y botones en acciones (`accion` contiene el patrón Command) |
| `sokoban.view` | Interfaz Swing: tablero, HUD y ventana |
| `sokoban.audio` | Efectos de sonido |
| `sokoban.logros` | Logros (Observer + Strategy) |
| `sokoban.persistencia` | Guardado y carga de partidas |

## Documentación de diseño

- [Registro de decisiones de diseño](docs/apuntes/decisiones-de-diseno.md): por qué elegimos cada patrón y cómo se aplica.
- [Resumen para el informe y la defensa](docs/apuntes/resumen-para-el-informe.md): tabla patrón → clases → pruebas, cobertura de requisitos y puntos pendientes.
- [Diagrama de clases](docs/uml/diagrama-de-clases.md): base en Mermaid para armar el diagrama de StarUML.
- [Assets del juego](docs/apuntes/assets.md): origen de imágenes y sonidos.
