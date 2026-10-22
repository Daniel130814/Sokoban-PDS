package sokoban.model.carga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.exception.NivelInvalidoException;
import sokoban.model.Nivel;
import sokoban.model.Posicion;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.TipoCelda;

class CargadorDeNivelTest {

    private final CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());

    @Test
    void cargaJugadorCajasYDestinosDesdeTexto() {
        Nivel nivel = cargador.cargar(List.of(
                "#####",
                "#@$.#",
                "#####"));

        assertEquals(new Posicion(1, 1), nivel.jugador().posicion());
        assertEquals(1, nivel.cajas().size());
        assertEquals(new Posicion(1, 2), nivel.cajas().get(0).posicion());
        assertEquals(TipoCelda.DESTINO, nivel.tablero().celdaEn(new Posicion(1, 3)));
    }

    @Test
    void lasFilasCortasSeCompletanConPared() {
        Nivel nivel = cargador.cargar(List.of("#####", "#@*", "#####"));

        assertEquals(TipoCelda.PARED, nivel.tablero().celdaEn(new Posicion(1, 4)));
        assertEquals(5, nivel.tablero().ancho());
    }

    @Test
    void elNivelDeEjemploSeCargaYSeResuelveConUnEmpuje() {
        Nivel nivel = cargador.cargarDesdeRecurso("/levels/nivel1.txt");

        assertTrue(nivel.moverJugador(Direccion.IZQUIERDA));

        Posicion cajaFinal = nivel.cajas().get(0).posicion();
        assertEquals(TipoCelda.DESTINO, nivel.tablero().celdaEn(cajaFinal));
    }

    @Test
    void rechazaCaracteresDesconocidos() {
        assertThrows(NivelInvalidoException.class,
                () -> cargador.cargar(List.of("#####", "#@$.X", "#####")));
    }

    @Test
    void rechazaNivelSinJugadorOConMasDeUno() {
        assertThrows(NivelInvalidoException.class, () -> cargador.cargar(List.of("#$.#")));
        assertThrows(NivelInvalidoException.class, () -> cargador.cargar(List.of("#@@$.#")));
    }

    @Test
    void rechazaCantidadDeCajasYDestinosDistinta() {
        assertThrows(NivelInvalidoException.class, () -> cargador.cargar(List.of("#@$$.#")));
    }

    @Test
    void rechazaNivelVacioORecursoInexistente() {
        assertThrows(NivelInvalidoException.class, () -> cargador.cargar(List.of()));
        assertThrows(NivelInvalidoException.class, () -> cargador.cargarDesdeRecurso("/levels/no-existe.txt"));
    }
}
