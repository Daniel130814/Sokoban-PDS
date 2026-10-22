package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sokoban.model.enums.TipoCelda.PARED;
import static sokoban.model.enums.TipoCelda.PISO;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.TipoCelda;

class NivelTest {

    // Fila 1 es el pasillo: columnas 1..5 son piso, el resto pared.
    private static Tablero pasillo() {
        return new Tablero(new TipoCelda[][] {
                {PARED, PARED, PARED, PARED, PARED, PARED, PARED},
                {PARED, PISO, PISO, PISO, PISO, PISO, PARED},
                {PARED, PARED, PARED, PARED, PARED, PARED, PARED}
        });
    }

    private static Nivel nivel(Posicion jugador, Caja... cajas) {
        return new Nivel(pasillo(), new Jugador(jugador), List.of(cajas));
    }

    @Test
    void elJugadorSeMueveSobreUnPisoLibre() {
        Nivel nivel = nivel(new Posicion(1, 1));

        assertTrue(nivel.moverJugador(Direccion.DERECHA));
        assertEquals(new Posicion(1, 2), nivel.jugador().posicion());
    }

    @Test
    void elJugadorNoAtraviesaParedes() {
        Nivel nivel = nivel(new Posicion(1, 1));

        assertFalse(nivel.moverJugador(Direccion.ARRIBA));
        assertEquals(new Posicion(1, 1), nivel.jugador().posicion());
    }

    @Test
    void elJugadorEmpujaUnaCaja() {
        Caja caja = new CajaNormal(new Posicion(1, 2));
        Nivel nivel = nivel(new Posicion(1, 1), caja);

        assertTrue(nivel.moverJugador(Direccion.DERECHA));

        assertEquals(new Posicion(1, 2), nivel.jugador().posicion());
        assertEquals(new Posicion(1, 3), caja.posicion());
    }

    @Test
    void noSePuedeEmpujarUnaCajaContraUnaPared() {
        Caja caja = new CajaNormal(new Posicion(1, 5));
        Nivel nivel = nivel(new Posicion(1, 4), caja);

        assertFalse(nivel.moverJugador(Direccion.DERECHA));

        assertEquals(new Posicion(1, 4), nivel.jugador().posicion());
        assertEquals(new Posicion(1, 5), caja.posicion());
    }

    @Test
    void noSePuedeEmpujarMasDeUnaCajaALaVez() {
        Caja primera = new CajaNormal(new Posicion(1, 2));
        Caja segunda = new CajaNormal(new Posicion(1, 3));
        Nivel nivel = nivel(new Posicion(1, 1), primera, segunda);

        assertFalse(nivel.moverJugador(Direccion.DERECHA));

        assertEquals(new Posicion(1, 1), nivel.jugador().posicion());
        assertEquals(new Posicion(1, 2), primera.posicion());
        assertEquals(new Posicion(1, 3), segunda.posicion());
    }

    @Test
    void elNivelSeResuelveCuandoTodasLasCajasEstanEnDestinos() {
        Tablero tablero = new Tablero(new TipoCelda[][] {
                {PARED, PARED, PARED, PARED, PARED},
                {PARED, PISO, PISO, TipoCelda.DESTINO, PARED},
                {PARED, PARED, PARED, PARED, PARED}
        });
        Nivel nivel = new Nivel(tablero, new Jugador(new Posicion(1, 1)),
                List.of(new CajaNormal(new Posicion(1, 2))));

        assertFalse(nivel.estaResuelto());
        nivel.moverJugador(Direccion.DERECHA);
        assertTrue(nivel.estaResuelto());
    }

    @Test
    void elJugadorNoTiraDeLasCajas() {
        Caja caja = new CajaNormal(new Posicion(1, 2));
        Nivel nivel = nivel(new Posicion(1, 3), caja);

        assertTrue(nivel.moverJugador(Direccion.DERECHA));

        assertEquals(new Posicion(1, 2), caja.posicion());
    }

    @Test
    void elNivelDelegaEnLaCajaQueHacerCuandoLaEmpujan() {
        // Una caja nueva se integra sin tocar Nivel (OCP) y respeta el contrato de Caja (LSP).
        int[] empujes = {0};
        Caja contadora = new Caja(new Posicion(1, 2)) {
            @Override
            public void alSerEmpujada() {
                empujes[0]++;
            }
        };
        Nivel nivel = nivel(new Posicion(1, 1), contadora);

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(1, empujes[0]);
    }
}
