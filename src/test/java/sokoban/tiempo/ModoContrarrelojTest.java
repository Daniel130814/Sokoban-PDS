package sokoban.tiempo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

class ModoContrarrelojTest {

    private final long[] ahora = {0};
    private final Cronometro cronometro = new Cronometro(() -> ahora[0]);

    /** Límite de 10 segundos y puntaje base fijo de 100 para aislar el ajuste del modo. */
    private Partida partida() {
        Partida partida = new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos())
                .cargar(List.of("#######", "#@ $ .#", "#######"))),
                estadisticas -> 100, new ModoContrarreloj(cronometro, Duration.ofSeconds(10)));
        partida.agregarObservador(cronometro);
        return partida;
    }

    @Test
    void sinMoverseElTiempoNoCorreYNoSePierde() {
        Partida partida = partida();
        ahora[0] = 500_000;

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }

    @Test
    void pasadoElLimiteElSiguienteMovimientoFallaElNivel() {
        Partida partida = partida();
        ahora[0] = 1_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 12_000;

        partida.mover(Direccion.DERECHA);

        assertEquals(EstadoPartida.NIVEL_FALLIDO, partida.estado());
    }

    @Test
    void terminarATiempoSumaUnPuntoPorSegundoQueSobra() {
        Partida partida = partida();
        ahora[0] = 1_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 4_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 6_000;
        partida.mover(Direccion.DERECHA);

        assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado());
        assertEquals(105, partida.resumenDelNivel().puntaje());
    }

    @Test
    void completarElNivelFueraDeTiempoIgualGanaPorqueSeEvaluaPrimero() {
        Partida partida = partida();
        ahora[0] = 1_000;
        partida.mover(Direccion.DERECHA);
        partida.mover(Direccion.DERECHA);
        ahora[0] = 50_000;
        partida.mover(Direccion.DERECHA);

        assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado());
        assertEquals(100, partida.resumenDelNivel().puntaje());
    }

    @Test
    void sinMoverseElJugadorPierdeAlRevisarElLimiteCuandoSeAgotaElTiempo() {
        Partida partida = partida();
        List<String> eventos = new ArrayList<>();
        partida.agregarObservador(new ObservadorDeJuego() {
            @Override
            public void nivelFallido() {
                eventos.add("fallido");
            }
        });
        ahora[0] = 1_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 8_000;
        partida.revisarLimite();
        assertEquals(EstadoPartida.JUGANDO, partida.estado());

        ahora[0] = 12_000;
        partida.revisarLimite();
        partida.revisarLimite();

        assertEquals(EstadoPartida.NIVEL_FALLIDO, partida.estado());
        assertEquals(List.of("fallido"), eventos);
    }

    @Test
    void revisarElLimiteAntesDelPrimerMovimientoNoHaceNada() {
        Partida partida = partida();
        ahora[0] = 900_000;

        partida.revisarLimite();

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }

    @Test
    void elHudMuestraCuantoTiempoQuedaYNoElQuePaso() {
        ModoContrarreloj modo = new ModoContrarreloj(cronometro, Duration.ofSeconds(90));

        assertEquals("Quedan 01:05", modo.etiquetaDeTiempo(Duration.ofSeconds(25)));
        assertEquals("Quedan 00:00", modo.etiquetaDeTiempo(Duration.ofSeconds(500)));
    }

    @Test
    void reintentarReiniciaElReloj() {
        Partida partida = partida();
        ahora[0] = 1_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 20_000;
        partida.mover(Direccion.DERECHA);

        partida.reiniciarNivel();
        ahora[0] = 21_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 25_000;
        partida.mover(Direccion.DERECHA);

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }
}
