package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class EventosDeJuegoTest {

    private static final CargadorDeNivel CARGADOR = new CargadorDeNivel(new FabricaDeElementos());

    /** Observador de prueba: anota el nombre de cada evento que recibe. */
    private static class Registro implements ObservadorDeJuego {
        final List<String> eventos = new ArrayList<>();

        @Override
        public void jugadorSeMovio() {
            eventos.add("jugadorSeMovio");
        }

        @Override
        public void cajaEmpujada() {
            eventos.add("cajaEmpujada");
        }

        @Override
        public void cajaSeRompio() {
            eventos.add("cajaSeRompio");
        }

        @Override
        public void cerrojoActivado() {
            eventos.add("cerrojoActivado");
        }

        @Override
        public void estadoRestaurado() {
            eventos.add("estadoRestaurado");
        }

        @Override
        public void nivelCompletado() {
            eventos.add("nivelCompletado");
        }

        @Override
        public void nivelCambiado() {
            eventos.add("nivelCambiado");
        }

        @Override
        public void juegoTerminado() {
            eventos.add("juegoTerminado");
        }
    }

    private static Partida partida(String... filas) {
        return new Partida(List.of(CARGADOR.cargar(List.of(filas))));
    }

    @Test
    void caminarSoloAvisaQueElJugadorSeMovio() {
        Partida partida = partida("#######", "#@  $.#", "#######");
        Registro registro = new Registro();
        partida.agregarObservador(registro);

        partida.mover(Direccion.DERECHA);

        assertEquals(List.of("jugadorSeMovio"), registro.eventos);
    }

    @Test
    void unMovimientoBloqueadoNoAvisaNada() {
        Partida partida = partida("#######", "#@  $.#", "#######");
        Registro registro = new Registro();
        partida.agregarObservador(registro);

        partida.mover(Direccion.ARRIBA);

        assertTrue(registro.eventos.isEmpty());
    }

    @Test
    void empujarAvisaElEmpujeYLuegoElMovimiento() {
        Partida partida = partida("#######", "#@$  .#", "#######");
        Registro registro = new Registro();
        partida.agregarObservador(registro);

        partida.mover(Direccion.DERECHA);

        assertEquals(List.of("cajaEmpujada", "jugadorSeMovio"), registro.eventos);
    }

    @Test
    void romperUnaCajaAvisaLaRotura() {
        Partida partida = partida("########", "#@%   .#", "########");
        Registro registro = new Registro();
        partida.agregarObservador(registro);

        for (int i = 0; i < CajaFragil.RESISTENCIA_INICIAL; i++) {
            partida.mover(Direccion.DERECHA);
        }

        assertTrue(registro.eventos.contains("cajaSeRompio"));
    }

    @Test
    void activarUnCerrojoAvisaAlObservador() {
        Partida partida = partida("#######", "#@a1A.#", "#######");
        Registro registro = new Registro();
        partida.agregarObservador(registro);

        partida.mover(Direccion.DERECHA);

        assertTrue(registro.eventos.contains("cerrojoActivado"));
    }

    @Test
    void deshacerYReiniciarAvisanQueSeRestauroElEstado() {
        Partida partida = partida("#######", "#@  $.#", "#######");
        partida.mover(Direccion.DERECHA);
        Registro registro = new Registro();
        partida.agregarObservador(registro);

        partida.deshacer();
        partida.reiniciarNivel();

        assertEquals(List.of("estadoRestaurado", "estadoRestaurado"), registro.eventos);
    }

    @Test
    void completarAvanzarYTerminarAvisanCadaEtapa() {
        Partida partida = new Partida(List.of(
                CARGADOR.cargar(List.of("#####", "#@$.#", "#####")),
                CARGADOR.cargar(List.of("#####", "#@$.#", "#####"))));
        Registro registro = new Registro();
        partida.agregarObservador(registro);

        partida.mover(Direccion.DERECHA);
        partida.avanzarDeNivel();
        partida.mover(Direccion.DERECHA);
        partida.avanzarDeNivel();

        assertEquals(List.of("cajaEmpujada", "jugadorSeMovio", "nivelCompletado", "nivelCambiado",
                "cajaEmpujada", "jugadorSeMovio", "nivelCompletado", "juegoTerminado"), registro.eventos);
    }
}
