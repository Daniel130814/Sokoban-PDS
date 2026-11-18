package sokoban.controller.accion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

class AccionTest {

    private static Partida partida() {
        return new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos())
                .cargar(List.of("#######", "#@  $.#", "#######"))));
    }

    @Test
    void moverEjecutaElMovimientoYDiceSiTuvoEfecto() {
        Partida partida = partida();

        assertTrue(new Mover(Direccion.DERECHA).ejecutar(partida));
        assertFalse(new Mover(Direccion.ARRIBA).ejecutar(partida));
    }

    @Test
    void deshacerSoloTieneEfectoSiHayHistorial() {
        Partida partida = partida();

        assertFalse(new Deshacer().ejecutar(partida));
        new Mover(Direccion.DERECHA).ejecutar(partida);
        assertTrue(new Deshacer().ejecutar(partida));
    }

    @Test
    void reiniciarVuelveAlInicioYContinuarSoloValeConElNivelCompletado() {
        Partida partida = partida();
        new Mover(Direccion.DERECHA).ejecutar(partida);

        assertTrue(new Reiniciar().ejecutar(partida));
        assertEquals(0, partida.nivelActual().movimientos());
        assertFalse(new Continuar().ejecutar(partida));

        new Mover(Direccion.DERECHA).ejecutar(partida);
        new Mover(Direccion.DERECHA).ejecutar(partida);
        new Mover(Direccion.DERECHA).ejecutar(partida);
        assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado());
        assertTrue(new Continuar().ejecutar(partida));
        assertEquals(EstadoPartida.JUEGO_TERMINADO, partida.estado());
    }

    @Test
    void irANivelEmpiezaElNivelPedidoYSoloValeDentroDeLaCantidadDeNiveles() {
        CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
        Partida dosNiveles = new Partida(List.of(
                cargador.cargar(List.of("#####", "#@$.#", "#####")),
                cargador.cargar(List.of("#####", "#@$.#", "#####"))));

        assertTrue(new IrANivel(2).ejecutar(dosNiveles));
        assertEquals(2, dosNiveles.numeroDeNivel());
        assertFalse(new IrANivel(3).ejecutar(dosNiveles));
        assertFalse(new IrANivel(0).ejecutar(dosNiveles));
        assertEquals(2, dosNiveles.numeroDeNivel());
        assertEquals("IR_A_NIVEL 2", new IrANivel(2).comoTexto());
    }

    @Test
    void cadaAccionSeDescribeComoTexto() {
        assertEquals("MOVER ARRIBA", new Mover(Direccion.ARRIBA).comoTexto());
        assertEquals("DESHACER", new Deshacer().comoTexto());
        assertEquals("REINICIAR", new Reiniciar().comoTexto());
        assertEquals("CONTINUAR", new Continuar().comoTexto());
    }
}
