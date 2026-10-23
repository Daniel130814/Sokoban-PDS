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

Compila y abre el juego. Se juega con las flechas del teclado; los botones permiten deshacer y reiniciar el nivel.

## Documentación de diseño

- [Registro de decisiones de diseño](docs/apuntes/decisiones-de-diseno.md): por qué elegimos cada patrón y cómo se aplica.
- [Assets del juego](docs/apuntes/assets.md): origen de imágenes y sonidos.
