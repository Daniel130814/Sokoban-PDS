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
    void continuarSoloAvanzaConElNivelCompletado() {
        controlador.continuar();

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }
}
