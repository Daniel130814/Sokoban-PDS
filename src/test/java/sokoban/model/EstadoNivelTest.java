package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class EstadoNivelTest {

    private static final CargadorDeNivel CARGADOR = new CargadorDeNivel(new FabricaDeElementos());

    @Test
    void restauraJugadorCajaYContadores() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@$  .#", "#######"));
        EstadoNivel antes = nivel.guardarEstado();

        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);
        nivel.restaurar(antes);

        assertEquals(new Posicion(1, 1), nivel.jugador().posicion());
        assertEquals(new Posicion(1, 2), nivel.cajas().get(0).posicion());
        assertEquals(0, nivel.movimientos());
        assertEquals(0, nivel.empujes());
    }

    @Test
    void losContadoresSumanMovimientosYEmpujes() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "# @$ .#", "#######"));

        nivel.moverJugador(Direccion.IZQUIERDA);
        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(3, nivel.movimientos());
        assertEquals(1, nivel.empujes());
    }

    @Test
    void elSnapshotNoCambiaSiSeSigueJugando() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@$  .#", "#######"));
        EstadoNivel antes = nivel.guardarEstado();

        nivel.moverJugador(Direccion.DERECHA);
        nivel.restaurar(antes);
        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);
        nivel.restaurar(antes);

        assertEquals(new Posicion(1, 1), nivel.jugador().posicion());
        assertEquals(new Posicion(1, 2), nivel.cajas().get(0).posicion());
    }

    @Test
    void restauraLaResistenciaYLaCajaRota() {
        Nivel nivel = CARGADOR.cargar(List.of("##########", "#@%     .#", "##########"));
        Caja caja = nivel.cajas().get(0);
        EstadoNivel antes = nivel.guardarEstado();

        for (int i = 0; i < CajaFragil.RESISTENCIA_INICIAL; i++) {
            nivel.moverJugador(Direccion.DERECHA);
        }
        assertTrue(nivel.quedoIrresoluble());

        nivel.restaurar(antes);

        assertFalse(nivel.quedoIrresoluble());
        assertEquals(1, nivel.cajas().size());
        assertTrue(nivel.cajas().get(0) == caja);
        assertEquals(CajaFragil.RESISTENCIA_INICIAL, ((CajaFragil) caja).resistencia());
        assertEquals(new Posicion(1, 2), caja.posicion());
    }

    @Test
    void restauraLosMurosYPermiteReactivarElCerrojo() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@a1A.#", "#######"));
        Posicion muro = new Posicion(1, 4);
        EstadoNivel antes = nivel.guardarEstado();

        nivel.moverJugador(Direccion.DERECHA);
        assertEquals(TipoCelda.MURO_ABIERTO, nivel.tablero().celdaEn(muro));

        nivel.restaurar(antes);
        assertEquals(TipoCelda.MURO_CERRADO, nivel.tablero().celdaEn(muro));

        nivel.moverJugador(Direccion.DERECHA);
        assertEquals(TipoCelda.MURO_ABIERTO, nivel.tablero().celdaEn(muro));
    }
}
