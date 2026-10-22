package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class LlaveYMuroTest {

    private static final CargadorDeNivel CARGADOR = new CargadorDeNivel(new FabricaDeElementos());
    private static final Posicion MURO = new Posicion(1, 4);

    @Test
    void elMuroCerradoBloqueaAlJugador() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@ A$.#", "#######"));

        assertTrue(nivel.moverJugador(Direccion.DERECHA));
        assertFalse(nivel.moverJugador(Direccion.DERECHA));
        assertEquals(TipoCelda.MURO_CERRADO, nivel.tablero().celdaEn(new Posicion(1, 3)));
    }

    @Test
    void laCajaLlaveSobreSuCerrojoAbreElMuro() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@a1A.#", "#######"));
        assertEquals(TipoCelda.MURO_CERRADO, nivel.tablero().celdaEn(MURO));

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(TipoCelda.MURO_ABIERTO, nivel.tablero().celdaEn(MURO));
    }

    @Test
    void conElMuroAbiertoLaCajaPuedeLlegarAlDestino() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@a1A.#", "#######"));

        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);
        nivel.moverJugador(Direccion.DERECHA);

        assertTrue(nivel.estaResuelto());
    }

    @Test
    void unaLlaveDeOtroIdNoAbreElMuro() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@b1A.#", "#######"));

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(TipoCelda.MURO_CERRADO, nivel.tablero().celdaEn(MURO));
    }

    @Test
    void unaCajaNormalSobreElCerrojoNoLoActiva() {
        Nivel nivel = CARGADOR.cargar(List.of("#######", "#@$1A.#", "#######"));

        nivel.moverJugador(Direccion.DERECHA);

        assertEquals(TipoCelda.MURO_CERRADO, nivel.tablero().celdaEn(MURO));
    }

    @Test
    void elCerrojoAvisaUnaSolaVezATodosSusObservadores() {
        Cerrojo cerrojo = new Cerrojo(1, new Posicion(0, 0));
        List<Cerrojo> avisos = new ArrayList<>();
        cerrojo.agregarObservador(avisos::add);
        cerrojo.agregarObservador(avisos::add);

        cerrojo.activar();
        cerrojo.activar();

        assertEquals(2, avisos.size());
        assertTrue(cerrojo.estaActivo());
    }
}
