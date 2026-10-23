# Diagrama de clases (base para StarUML)

El enunciado (según la skill, sin confirmar) pide el diagrama en **StarUML**. Este archivo es una base en
Mermaid, que GitHub dibuja directamente, para copiar la estructura a StarUML. **No es el entregable final:**
falta armar el diagrama en StarUML y revisar que coincida con el código. Está dividido por tema para que
se lea; los nombres de clases y métodos son los del código.

## Núcleo del juego

```mermaid
classDiagram
    class Partida {
        +mover(Direccion) boolean
        +deshacer() boolean
        +reiniciarNivel()
        +avanzarDeNivel()
        +resumenDelNivel() ResumenDeNivel
        +reconstruir(Consumer)
        +agregarObservador(ObservadorDeJuego)
    }
    class SesionDeNivel {
        +mover(Direccion) boolean
        +deshacer() boolean
        +reiniciar()
    }
    class Nivel {
        +moverJugador(Direccion) boolean
        +estaResuelto() boolean
        +quedoIrresoluble() boolean
        +guardarEstado() EstadoNivel
        +restaurar(EstadoNivel)
    }
    class Tablero
    class Jugador
    class Posicion
    class Cerrojo
    class Muro
    class Caja {
        <<abstract>>
        +alSerEmpujada()*
        +alAsentarseEn(Cerrojo)
        +estaRota() boolean
    }
    class CajaNormal
    class CajaFragil
    class CajaLlave
    class TipoCelda {
        <<enumeration>>
    }
    class Direccion {
        <<enumeration>>
    }
    class EstadoPartida {
        <<enumeration>>
    }

    Partida "1" o-- "1..*" Nivel
    Partida --> SesionDeNivel
    Partida --> EstadoPartida
    SesionDeNivel --> Nivel
    Nivel --> Tablero
    Nivel --> Jugador
    Nivel "1" o-- "*" Caja
    Nivel "1" o-- "*" Cerrojo
    Tablero --> TipoCelda
    Jugador --> Posicion
    Caja --> Posicion
    Caja <|-- CajaNormal
    Caja <|-- CajaFragil
    Caja <|-- CajaLlave
    Muro ..> Tablero
```

## Strategy: terreno y puntaje

```mermaid
classDiagram
    class ReglaDeMovimiento {
        <<interface>>
        +posicionFinal(llegada, direccion, tablero, ocupada) Posicion
    }
    class MovimientoNormal
    class Deslizamiento
    class TipoCelda {
        <<enumeration>>
        +reglaDeMovimiento() ReglaDeMovimiento
    }
    class Nivel
    class EstrategiaDePuntaje {
        <<interface>>
        +calcular(EstadisticasDeNivel) int
    }
    class PuntajePorEficiencia
    class Partida {
        +resumenDelNivel() ResumenDeNivel
    }

    ReglaDeMovimiento <|.. MovimientoNormal
    ReglaDeMovimiento <|.. Deslizamiento
    TipoCelda --> ReglaDeMovimiento
    Nivel ..> ReglaDeMovimiento : ejecuta la de la celda
    EstrategiaDePuntaje <|.. PuntajePorEficiencia
    Partida --> EstrategiaDePuntaje
```

## Memento: deshacer y reiniciar

```mermaid
classDiagram
    class Nivel {
        <<Originator>>
        +guardarEstado() EstadoNivel
        +restaurar(EstadoNivel)
    }
    class EstadoNivel {
        <<Memento>>
    }
    class EstadoCaja {
        <<interface>>
        +restaurar()
    }
    class HistorialDeEstados {
        <<Caretaker>>
        +guardar(EstadoNivel)
        +retroceder(int) Optional
    }
    class SesionDeNivel {
        +deshacer() boolean
        +reiniciar()
    }

    Nivel ..> EstadoNivel : crea y lee
    EstadoNivel o-- EstadoCaja
    HistorialDeEstados o-- EstadoNivel
    SesionDeNivel --> HistorialDeEstados
    SesionDeNivel --> Nivel
```

## Observer: eventos, cerrojos, vista, sonido y logros

```mermaid
classDiagram
    class ObservadorDeJuego {
        <<interface>>
    }
    class DifusorDeEventos {
        +agregar(ObservadorDeJuego)
        +silenciar(boolean)
    }
    class Nivel
    class Partida
    class PanelDeTablero
    class PanelHud
    class VentanaPrincipal
    class SonidosDelJuego
    class Logros
    class ObservadorDeCerrojo {
        <<interface>>
    }
    class Cerrojo {
        <<Subject>>
        +activar()
    }
    class Muro

    ObservadorDeJuego <|.. DifusorDeEventos
    ObservadorDeJuego <|.. PanelDeTablero
    ObservadorDeJuego <|.. PanelHud
    ObservadorDeJuego <|.. VentanaPrincipal
    ObservadorDeJuego <|.. SonidosDelJuego
    ObservadorDeJuego <|.. Logros
    Nivel --> DifusorDeEventos
    Partida --> DifusorDeEventos
    ObservadorDeCerrojo <|.. Muro
    ObservadorDeCerrojo <|.. Nivel
    Cerrojo o-- ObservadorDeCerrojo
```

## Controlador, Command, vista y audio

```mermaid
classDiagram
    class ControladorDeJuego {
        +ejecutar(Accion)
        +guardar(Path)
        +cargar(Path)
    }
    class Accion {
        <<interface>>
        +ejecutar(Partida) boolean
        +comoTexto() String
    }
    class Mover
    class Deshacer
    class Reiniciar
    class Continuar
    class GuardadoDePartida
    class Partida
    class RepositorioDeImagenes {
        <<Flyweight Factory>>
    }
    class SelectorDeSprites
    class PanelDeTablero
    class ReproductorDeSonido {
        <<interface>>
    }
    class ReproductorJavaSound
    class SonidosDelJuego
    class CriterioDeLogro {
        <<interface>>
    }
    class Logro
    class Logros

    ControladorDeJuego --> Partida
    ControladorDeJuego --> Accion
    ControladorDeJuego --> GuardadoDePartida
    Accion <|.. Mover
    Accion <|.. Deshacer
    Accion <|.. Reiniciar
    Accion <|.. Continuar
    PanelDeTablero --> RepositorioDeImagenes
    PanelDeTablero --> SelectorDeSprites
    PanelDeTablero --> ControladorDeJuego
    ReproductorDeSonido <|.. ReproductorJavaSound
    SonidosDelJuego --> ReproductorDeSonido
    Logros o-- Logro
    Logro --> CriterioDeLogro
```

## Carga de niveles: Simple Factory

```mermaid
classDiagram
    class CargadorDeNivel {
        +cargar(List) Nivel
        +cargarDesdeRecurso(String) Nivel
    }
    class FabricaDeElementos {
        <<Simple Factory>>
        +crearCelda(char) TipoCelda
        +crearCaja(char, Posicion) Optional
        +esJugador(char) boolean
    }
    class Nivel

    CargadorDeNivel --> FabricaDeElementos
    CargadorDeNivel ..> Nivel : construye
    FabricaDeElementos ..> Caja : crea
```
