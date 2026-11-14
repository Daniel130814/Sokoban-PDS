package sokoban.model.modo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;
import sokoban.model.puntaje.PuntajePorEficiencia;

class ModoDeJuegoTest {

    private static final List<String> NIVEL = List.of("#######", "#@ $ .#", "#######");

    /** El nivel se resuelve con exactamente tres movimientos a la derecha. */
    private static Partida partida(ModoDeJuego modo) {
        return new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos()).cargar(NIVEL)),
                new PuntajePorEficiencia(), modo);
    }

    private static void mover(Partida partida, int veces) {
        for (int i = 0; i < veces; i++) {
            partida.mover(Direccion.DERECHA);
        }
    }

    @Test
    void elModoClasicoNuncaFalla() {
        Partida partida = partida(new ModoClasico());

        for (int i = 0; i < 50; i++) {
            partida.mover(Direccion.IZQUIERDA);
            partida.mover(Direccion.ARRIBA);
            partida.mover(Direccion.ABAJO);
        }

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }

    @Test
    void sinModoElegidoLaPartidaJuegaEnModoClasico() {
        Partida partida = new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos()).cargar(NIVEL)));

        assertEquals("Clásico", partida.modo().nombre());
    }

    @Test
    void alAgotarLosMovimientosSinResolverElNivelFalla() {
        Partida partida = partida(new ModoMovimientosLimitados(2));
        List<String> eventos = new ArrayList<>();
        partida.agregarObservador(new ObservadorDeJuego() {
            @Override
            public void nivelFallido() {
                eventos.add("fallido");
            }
        });

        mover(partida, 2);

        assertEquals(EstadoPartida.NIVEL_FALLIDO, partida.estado());
        assertEquals(List.of("fallido"), eventos);
        assertFalse(partida.mover(Direccion.DERECHA));
        assertThrows(IllegalStateException.class, partida::resumenDelNivel);
    }

    @Test
    void resolverElNivelEnElUltimoMovimientoPermitidoGana() {
        Partida partida = partida(new ModoMovimientosLimitados(3));

        mover(partida, 3);

        assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado());
    }

    @Test
    void reintentarUnNivelFallidoLoDejaJugable() {
        Partida partida = partida(new ModoMovimientosLimitados(2));
        mover(partida, 2);
        List<String> eventos = new ArrayList<>();
        partida.agregarObservador(new ObservadorDeJuego() {
            @Override
            public void nivelCambiado() {
                eventos.add("cambiado");
            }
        });

        partida.reiniciarNivel();

        assertEquals(EstadoPartida.JUGANDO, partida.estado());
        assertEquals(0, partida.nivelActual().movimientos());
        assertEquals(List.of("cambiado"), eventos);
    }

    @Test
    void deshacerDevuelveLosMovimientosAlContador() {
        Partida partida = partida(new ModoMovimientosLimitados(3));
        mover(partida, 2);

        partida.deshacer();

        assertEquals(0, partida.nivelActual().movimientos());
        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }

    @Test
    void elContadorDelHudMuestraElTopeSoloEnElModoLimitado() {
        Partida limitada = partida(new ModoMovimientosLimitados(10));
        Partida clasica = partida(new ModoClasico());
        limitada.mover(Direccion.DERECHA);
        clasica.mover(Direccion.DERECHA);

        assertEquals("1/10", limitada.modo().etiquetaDeMovimientos(limitada.nivelActual()));
        assertEquals("1", clasica.modo().etiquetaDeMovimientos(clasica.nivelActual()));
    }

    @Test
    void elMaximoDeMovimientosDebeSerPositivo() {
        assertThrows(IllegalArgumentException.class, () -> new ModoMovimientosLimitados(0));
    }

    @Test
    void cambiarDeModoCambiaLasReglasDeFinDeNivel() {
        Partida partida = partida(new ModoClasico());

        partida.establecerModo(new ModoMovimientosLimitados(1));
        partida.mover(Direccion.DERECHA);

        assertEquals(EstadoPartida.NIVEL_FALLIDO, partida.estado());
    }
}
