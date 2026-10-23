package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;

class HistorialDeEstadosTest {

    private final Nivel nivel = new CargadorDeNivel(new FabricaDeElementos())
            .cargar(List.of("#####", "#@$.#", "#####"));

    private List<EstadoNivel> guardar(HistorialDeEstados historial, int cantidad) {
        List<EstadoNivel> guardados = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            EstadoNivel estado = nivel.guardarEstado();
            guardados.add(estado);
            historial.guardar(estado);
        }
        return guardados;
    }

    @Test
    void conservaSoloLosUltimosEstadosHastaLaCapacidad() {
        HistorialDeEstados historial = new HistorialDeEstados();

        guardar(historial, HistorialDeEstados.CAPACIDAD + 5);

        assertEquals(HistorialDeEstados.CAPACIDAD, historial.cantidad());
    }

    @Test
    void retrocederDevuelveElMasAntiguoDeLosRetirados() {
        HistorialDeEstados historial = new HistorialDeEstados();
        List<EstadoNivel> guardados = guardar(historial, 8);

        EstadoNivel restaurado = historial.retroceder(5).orElseThrow();

        assertSame(guardados.get(3), restaurado);
        assertEquals(3, historial.cantidad());
    }

    @Test
    void siHayMenosDeLosPedidosRetrocedeLoQueHay() {
        HistorialDeEstados historial = new HistorialDeEstados();
        List<EstadoNivel> guardados = guardar(historial, 2);

        EstadoNivel restaurado = historial.retroceder(5).orElseThrow();

        assertSame(guardados.get(0), restaurado);
        assertEquals(0, historial.cantidad());
    }

    @Test
    void sinEstadosNoHayAdondeRetroceder() {
        assertTrue(new HistorialDeEstados().retroceder(5).isEmpty());
    }
}
