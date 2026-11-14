package sokoban.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.Partida;
import sokoban.model.Posicion;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;
import sokoban.model.modo.ModoMovimientosLimitados;

class ControladorDeJuegoTest {

    private final Partida partida = new Partida(List.of(
            new CargadorDeNivel(new FabricaDeElementos()).cargar(List.of("#######", "#@  $.#", "#######"))));
    private final ControladorDeJuego controlador = new ControladorDeJuego(partida);

    @Test
    void trasladaMovimientosDeshacerYReinicioAlModelo() {
        controlador.mover(Direccion.DERECHA);
        assertEquals(new Posicion(1, 2), partida.nivelActual().jugador().posicion());

        controlador.deshacer();
        assertEquals(new Posicion(1, 1), partida.nivelActual().jugador().posicion());

        controlador.mover(Direccion.DERECHA);
        controlador.reiniciarNivel();
        assertEquals(0, partida.nivelActual().movimientos());
    }

    @Test
    void unaNuevaPartidaDespuesDelFinalVuelveAlPrimerNivelSinJugadas() {
        controlador.mover(Direccion.DERECHA);
        controlador.mover(Direccion.DERECHA);
        controlador.mover(Direccion.DERECHA);
        controlador.continuar();
        assertEquals(EstadoPartida.JUEGO_TERMINADO, partida.estado());

        controlador.nuevaPartida();

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
        assertEquals(1, partida.numeroDeNivel());
        assertEquals(0, partida.nivelActual().movimientos());
        assertEquals(new Posicion(1, 1), partida.nivelActual().jugador().posicion());
    }

    @Test
    void elegirUnModoEmpiezaDeNuevoConSusReglasYSePuedeReintentarTrasFallar() {
        controlador.mover(Direccion.DERECHA);

        controlador.elegirModo(new ModoMovimientosLimitados(1));

        assertEquals(0, partida.nivelActual().movimientos());
        controlador.mover(Direccion.DERECHA);
        assertEquals(EstadoPartida.NIVEL_FALLIDO, partida.estado());
        controlador.reiniciarNivel();
        assertEquals(EstadoPartida.JUGANDO, partida.estado());
        assertEquals(0, partida.nivelActual().movimientos());
    }

    @Test
    void continuarSoloAvanzaConElNivelCompletado() {
        controlador.continuar();

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }
}
