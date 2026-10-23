package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class SesionDeNivelTest {

    // Pasillo largo: el jugador camina hacia la derecha sin chocar, 1 columna por movimiento.
    private static SesionDeNivel sesion() {
        Nivel nivel = new CargadorDeNivel(new FabricaDeElementos()).cargar(List.of(
                "#".repeat(42),
                "#@" + " ".repeat(30) + "$." + " ".repeat(7) + "#",
                "#".repeat(42)));
        return new SesionDeNivel(nivel);
    }

    private static void mover(SesionDeNivel sesion, int veces) {
        for (int i = 0; i < veces; i++) {
            assertTrue(sesion.mover(Direccion.DERECHA));
        }
    }

    private static int columna(SesionDeNivel sesion) {
        return sesion.nivel().jugador().posicion().columna();
    }

    @Test
    void deshacerRetrocedeCincoMovimientos() {
        SesionDeNivel sesion = sesion();
        mover(sesion, 8);

        assertTrue(sesion.deshacer());

        assertEquals(1 + 3, columna(sesion));
        assertEquals(3, sesion.nivel().movimientos());
    }

    @Test
    void conMenosDeCincoMovimientosVuelveAlInicio() {
        SesionDeNivel sesion = sesion();
        mover(sesion, 2);

        assertTrue(sesion.deshacer());

        assertEquals(1, columna(sesion));
    }

    @Test
    void sinMovimientosNoHayNadaQueDeshacer() {
        assertFalse(sesion().deshacer());
    }

    @Test
    void soloSePermitenTresUsosConsecutivos() {
        SesionDeNivel sesion = sesion();
        mover(sesion, 20);

        assertTrue(sesion.deshacer());
        assertTrue(sesion.deshacer());
        assertTrue(sesion.deshacer());
        assertFalse(sesion.deshacer());

        assertEquals(3, sesion.usosDeDeshacer());
    }

    @Test
    void unMovimientoReiniciaLaCuentaDeUsosConsecutivos() {
        SesionDeNivel sesion = sesion();
        mover(sesion, 20);
        sesion.deshacer();
        sesion.deshacer();
        sesion.deshacer();

        mover(sesion, 1);

        assertTrue(sesion.deshacer());
    }

    @Test
    void elHistorialGuardaSoloLosUltimosQuinceMovimientos() {
        SesionDeNivel sesion = sesion();
        mover(sesion, 20);

        sesion.deshacer();
        sesion.deshacer();
        sesion.deshacer();

        // 20 movimientos, historial de 15: no se puede volver antes del movimiento 5.
        assertEquals(1 + 5, columna(sesion));
    }

    @Test
    void unMovimientoBloqueadoNoSeGuardaEnElHistorial() {
        SesionDeNivel sesion = sesion();

        assertFalse(sesion.mover(Direccion.ARRIBA));

        assertFalse(sesion.deshacer());
    }

    @Test
    void reiniciarVuelveAlEstadoInicialYLimpiaElHistorial() {
        SesionDeNivel sesion = sesion();
        mover(sesion, 7);
        sesion.deshacer();

        sesion.reiniciar();

        assertEquals(1, columna(sesion));
        assertEquals(0, sesion.nivel().movimientos());
        assertEquals(0, sesion.usosDeDeshacer());
        assertFalse(sesion.deshacer());
    }
}
