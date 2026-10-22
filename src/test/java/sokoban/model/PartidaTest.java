package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

class PartidaTest {

    private static final CargadorDeNivel CARGADOR = new CargadorDeNivel(new FabricaDeElementos());

    /** Nivel que se resuelve con un único empuje a la derecha. */
    private static Nivel nivelDeUnEmpuje() {
        return CARGADOR.cargar(List.of("#####", "#@$.#", "#####"));
    }

    private static Partida partidaDeDosNiveles() {
        return new Partida(List.of(nivelDeUnEmpuje(), nivelDeUnEmpuje()));
    }

    @Test
    void empiezaJugandoEnElPrimerNivel() {
        Partida partida = partidaDeDosNiveles();

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
        assertEquals(1, partida.numeroDeNivel());
    }

    @Test
    void alPonerLasCajasEnSusDestinosElNivelQuedaCompletado() {
        Partida partida = partidaDeDosNiveles();

        assertTrue(partida.mover(Direccion.DERECHA));

        assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado());
    }

    @Test
    void conElNivelCompletadoSeIgnoranLosMovimientos() {
        Partida partida = partidaDeDosNiveles();
        partida.mover(Direccion.DERECHA);
        Posicion antes = partida.nivelActual().jugador().posicion();

        assertFalse(partida.mover(Direccion.IZQUIERDA));

        assertEquals(antes, partida.nivelActual().jugador().posicion());
    }

    @Test
    void avanzarPasaAlSiguienteNivel() {
        Partida partida = partidaDeDosNiveles();
        partida.mover(Direccion.DERECHA);

        partida.avanzarDeNivel();

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
        assertEquals(2, partida.numeroDeNivel());
    }

    @Test
    void despuesDelUltimoNivelElJuegoTermina() {
        Partida partida = new Partida(List.of(nivelDeUnEmpuje()));
        partida.mover(Direccion.DERECHA);

        partida.avanzarDeNivel();

        assertEquals(EstadoPartida.JUEGO_TERMINADO, partida.estado());
        assertFalse(partida.mover(Direccion.IZQUIERDA));
    }

    @Test
    void noSePuedeAvanzarSiElNivelNoEstaCompletado() {
        Partida partida = partidaDeDosNiveles();

        assertThrows(IllegalStateException.class, partida::avanzarDeNivel);
    }

    @Test
    void rechazaUnaPartidaSinNiveles() {
        assertThrows(IllegalArgumentException.class, () -> new Partida(List.of()));
    }
}
