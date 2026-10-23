package sokoban.model.puntaje;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class PuntajeTest {

    private static final CargadorDeNivel CARGADOR = new CargadorDeNivel(new FabricaDeElementos());

    private static Partida partida(EstrategiaDePuntaje estrategia) {
        return new Partida(List.of(CARGADOR.cargar(List.of("#######", "#@ $ .#", "#######"))), estrategia);
    }

    private static void resolver(Partida partida) {
        partida.mover(Direccion.DERECHA);
        partida.mover(Direccion.DERECHA);
        partida.mover(Direccion.DERECHA);
    }

    @Test
    void laEficienciaDescuentaMovimientosEmpujesYDeshacer() {
        EstrategiaDePuntaje estrategia = new PuntajePorEficiencia();

        int puntaje = estrategia.calcular(new EstadisticasDeNivel(10, 4, 2));

        assertEquals(1000 - 10 * 10 - 4 * 5 - 2 * 50, puntaje);
    }

    @Test
    void elPuntajeNuncaEsNegativo() {
        assertEquals(0, new PuntajePorEficiencia().calcular(new EstadisticasDeNivel(500, 0, 0)));
    }

    @Test
    void usarDeshacerBajaElPuntaje() {
        EstrategiaDePuntaje estrategia = new PuntajePorEficiencia();

        int sinDeshacer = estrategia.calcular(new EstadisticasDeNivel(5, 2, 0));
        int conDeshacer = estrategia.calcular(new EstadisticasDeNivel(5, 2, 1));

        assertTrue(conDeshacer < sinDeshacer);
    }

    @Test
    void elResumenSeArmaConLasEstadisticasRealesDelNivel() {
        Partida partida = partida(new PuntajePorEficiencia());
        resolver(partida);

        ResumenDeNivel resumen = partida.resumenDelNivel();

        assertEquals(new EstadisticasDeNivel(3, 2, 0), resumen.estadisticas());
        assertEquals(1000 - 30 - 10, resumen.puntaje());
    }

    @Test
    void sePuedeCambiarElCriterioSinTocarLaPartida() {
        Partida partida = partida(estadisticas -> estadisticas.movimientos());
        resolver(partida);

        assertEquals(3, partida.resumenDelNivel().puntaje());
    }

    @Test
    void sinCompletarElNivelNoHayResumen() {
        Partida partida = partida(new PuntajePorEficiencia());

        assertThrows(IllegalStateException.class, partida::resumenDelNivel);
    }
}
