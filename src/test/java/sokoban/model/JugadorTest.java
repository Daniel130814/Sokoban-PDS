package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sokoban.model.enums.TipoCelda.DESTINO;
import static sokoban.model.enums.TipoCelda.PARED;
import static sokoban.model.enums.TipoCelda.PISO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.TipoCelda;

class JugadorTest {

    private Tablero tablero;

    @BeforeEach
    void armarTablero() {
        tablero = new Tablero(new TipoCelda[][] {
                {PARED, PARED, PARED, PARED},
                {PARED, PISO, DESTINO, PARED},
                {PARED, PARED, PARED, PARED}
        });
    }

    @Test
    void sePuedeMoverSobreUnPisoLibre() {
        Jugador jugador = new Jugador(new Posicion(1, 1));

        assertTrue(jugador.mover(Direccion.DERECHA, tablero));
        assertEquals(new Posicion(1, 2), jugador.posicion());
    }

    @Test
    void noAtraviesaParedesYNoCambiaDePosicion() {
        Jugador jugador = new Jugador(new Posicion(1, 1));

        assertFalse(jugador.mover(Direccion.ARRIBA, tablero));
        assertEquals(new Posicion(1, 1), jugador.posicion());
    }

    @Test
    void noSaleDelTablero() {
        Tablero abierto = new Tablero(new TipoCelda[][] {{PISO}});
        Jugador jugador = new Jugador(new Posicion(0, 0));

        assertFalse(jugador.mover(Direccion.IZQUIERDA, abierto));
        assertEquals(new Posicion(0, 0), jugador.posicion());
    }

    @Test
    void tableroRechazaFilasDeDistintoLargo() {
        TipoCelda[][] irregular = {{PISO, PISO}, {PISO}};

        assertThrows(IllegalArgumentException.class, () -> new Tablero(irregular));
    }

    @Test
    void tableroNoCambiaSiSeModificaElArregloOriginal() {
        TipoCelda[][] celdas = {{PISO, PISO}};
        Tablero copia = new Tablero(celdas);

        celdas[0][1] = PARED;

        assertTrue(copia.esTransitable(new Posicion(0, 1)));
    }
}
