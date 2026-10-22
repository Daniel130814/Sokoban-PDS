package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sokoban.model.TipoCelda.DESTINO;
import static sokoban.model.TipoCelda.HIELO;
import static sokoban.model.TipoCelda.PARED;
import static sokoban.model.TipoCelda.PISO;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.enums.Direccion;

class DeslizamientoTest {

    /** Arma un pasillo horizontal en la fila 1 con el jugador en la columna 1 y la caja en la 2. */
    private static Nivel pasillo(Caja caja, TipoCelda... pasillo) {
        TipoCelda[] fila = new TipoCelda[pasillo.length + 2];
        fila[0] = PARED;
        fila[fila.length - 1] = PARED;
        System.arraycopy(pasillo, 0, fila, 1, pasillo.length);
        TipoCelda[] muro = new TipoCelda[fila.length];
        Arrays.fill(muro, PARED);
        Tablero tablero = new Tablero(new TipoCelda[][] {muro, fila, muro});
        return new Nivel(tablero, new Jugador(new Posicion(1, 1)), List.of(caja));
    }

    @Test
    void laCajaSeDeslizaSobreHieloHastaLaPared() {
        Caja caja = new CajaNormal(new Posicion(1, 2));
        Nivel nivel = pasillo(caja, PISO, PISO, HIELO, HIELO, HIELO);

        assertTrue(nivel.moverJugador(Direccion.DERECHA));

        assertEquals(new Posicion(1, 5), caja.posicion());
        assertEquals(new Posicion(1, 2), nivel.jugador().posicion());
    }

    @Test
    void laCajaSeDetieneAlSalirDelHieloAUnPiso() {
        Caja caja = new CajaNormal(new Posicion(1, 2));
        Nivel nivel = pasillo(caja, PISO, PISO, HIELO, HIELO, PISO, PISO);

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(new Posicion(1, 5), caja.posicion());
    }

    @Test
    void laCajaSeDetieneAlLlegarAUnDestino() {
        Caja caja = new CajaNormal(new Posicion(1, 2));
        Nivel nivel = pasillo(caja, PISO, PISO, HIELO, HIELO, DESTINO, PISO);

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(new Posicion(1, 5), caja.posicion());
        assertTrue(nivel.estaResuelto());
    }

    @Test
    void laCajaSeDetieneAntesDeOtraCaja() {
        Caja caja = new CajaNormal(new Posicion(1, 2));
        Caja obstaculo = new CajaNormal(new Posicion(1, 5));
        TipoCelda[] fila = {PARED, PISO, PISO, HIELO, HIELO, HIELO, HIELO, PARED};
        TipoCelda[] muro = {PARED, PARED, PARED, PARED, PARED, PARED, PARED, PARED};
        Nivel nivel = new Nivel(new Tablero(new TipoCelda[][] {muro, fila, muro}),
                new Jugador(new Posicion(1, 1)), List.of(caja, obstaculo));

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(new Posicion(1, 4), caja.posicion());
    }

    @Test
    void elDeslizamientoCuentaComoUnSoloEmpuje() {
        int[] empujes = {0};
        Caja caja = new Caja(new Posicion(1, 2)) {
            @Override
            public void alSerEmpujada() {
                empujes[0]++;
            }
        };
        Nivel nivel = pasillo(caja, PISO, PISO, HIELO, HIELO, HIELO);

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(1, empujes[0]);
    }

    @Test
    void sobrePisoNormalLaCajaAvanzaUnaSolaCelda() {
        Caja caja = new CajaNormal(new Posicion(1, 2));
        Nivel nivel = pasillo(caja, PISO, PISO, PISO, PISO);

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(new Posicion(1, 3), caja.posicion());
    }
}
