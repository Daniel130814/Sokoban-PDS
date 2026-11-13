package sokoban.tiempo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class CronometroTest {

    private final long[] ahora = {0};
    private final Cronometro cronometro = new Cronometro(() -> ahora[0]);

    private Partida partida() {
        Partida partida = new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos())
                .cargar(List.of("#######", "#@ $ .#", "#######"))));
        partida.agregarObservador(cronometro);
        return partida;
    }

    @Test
    void noCuentaHastaElPrimerMovimiento() {
        partida();
        ahora[0] = 50_000;

        assertEquals(Duration.ZERO, cronometro.transcurrido());
    }

    @Test
    void cuentaDesdeElPrimerMovimientoYSeDetieneAlCompletarElNivel() {
        Partida partida = partida();
        ahora[0] = 1_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 6_000;
        assertEquals(Duration.ofSeconds(5), cronometro.transcurrido());

        partida.mover(Direccion.DERECHA);
        ahora[0] = 8_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 99_000;

        assertEquals(Duration.ofSeconds(7), cronometro.transcurrido());
    }

    @Test
    void cambiarDeNivelLoPoneEnCero() {
        ahora[0] = 1_000;
        cronometro.jugadorSeMovio();
        ahora[0] = 4_000;

        cronometro.nivelCambiado();

        assertEquals(Duration.ZERO, cronometro.transcurrido());
    }

    @Test
    void sePresentaComoMinutosYSegundos() {
        ahora[0] = 0;
        cronometro.jugadorSeMovio();
        ahora[0] = 65_000;

        assertEquals("01:05", cronometro.comoTexto());
    }
}
