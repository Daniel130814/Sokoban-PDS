package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class CajaFragilTest {

    private static final CargadorDeNivel CARGADOR = new CargadorDeNivel(new FabricaDeElementos());

    @Test
    void cadaEmpujeReduceLaResistencia() {
        CajaFragil caja = new CajaFragil(new Posicion(1, 2), 3);

        caja.alSerEmpujada();

        assertEquals(2, caja.resistencia());
        assertFalse(caja.estaRota());
    }

    @Test
    void alLlegarACeroLaCajaSeRompeYDesapareceDelNivel() {
        Nivel nivel = CARGADOR.cargar(List.of("########", "#@%   .#", "########"));
        Caja caja = nivel.cajas().get(0);

        for (int i = 0; i < CajaFragil.RESISTENCIA_INICIAL - 1; i++) {
            assertTrue(nivel.moverJugador(Direccion.DERECHA));
        }
        assertEquals(1, nivel.cajas().size());

        assertTrue(nivel.moverJugador(Direccion.DERECHA));

        assertTrue(caja.estaRota());
        assertTrue(nivel.cajas().isEmpty());
    }

    @Test
    void sinCajaRestanteElNivelQuedaIrresoluble() {
        Nivel nivel = CARGADOR.cargar(List.of("########", "#@%   .#", "########"));

        for (int i = 0; i < CajaFragil.RESISTENCIA_INICIAL; i++) {
            nivel.moverJugador(Direccion.DERECHA);
        }

        assertTrue(nivel.quedoIrresoluble());
        assertFalse(nivel.estaResuelto());
    }

    @Test
    void unaCajaNormalNuncaSeRompe() {
        Nivel nivel = CARGADOR.cargar(List.of("########", "#@$   .#", "########"));

        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(1, nivel.cajas().size());
        assertFalse(nivel.quedoIrresoluble());
    }

    @Test
    void rechazaResistenciaInicialInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new CajaFragil(new Posicion(0, 0), 0));
    }
}
