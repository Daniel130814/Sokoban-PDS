package sokoban;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import sokoban.model.CajaFragil;
import sokoban.model.Partida;
import sokoban.model.campania.CampaniaEstandar;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

/**
 * Prueba de aceptación de los niveles 5 a 9 de la campaña. Las soluciones de los niveles 5 a 7 salen de una
 * búsqueda exhaustiva (mínimo de movimientos). El nivel 8 no tiene solución conocida: ver DD-017.
 * Letras: D derecha, I izquierda, A arriba, B abajo.
 */
class NivelesDeDesafioTest {

    private static final Map<Integer, String> SOLUCIONES = Map.of(
            5, "DDBBBDDAIDAIIBAAIBB",
            6, "DAAAIIIBBIIAADIBBDDAADDDAAIBDBIIIBBIIAADDDDBDBBIAADAIAADBBBIAIIBBIIAADDDDBDAIIIIIAADDBAIIBBDDDDBBBDA",
            7, "DDDIIIBBDDDAABBBDDAIBIIAIIAADDDDDADDBIIIIBBIBDAAAIAADBBBBIIIAADDIIBBDDDAABBIBBDAADDBI");

    private static Direccion direccion(char letra) {
        return switch (letra) {
            case 'D' -> Direccion.DERECHA;
            case 'I' -> Direccion.IZQUIERDA;
            case 'A' -> Direccion.ARRIBA;
            default -> Direccion.ABAJO;
        };
    }

    @Test
    void laCampaniaCargaLosNueveNivelesComoUnaPartida() {
        CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());

        Partida partida = new Partida(CampaniaEstandar.crear(cargador::cargarDesdeRecurso).niveles());

        assertEquals(9, CampaniaEstandar.crear(cargador::cargarDesdeRecurso).cantidadDeNiveles());
        assertEquals(EstadoPartida.JUGANDO, partida.estado());
    }

    @Test
    void elNivel9SeResuelveAbriendoElMuroConLaCajaLlaveYSinRomperLasFragiles() {
        CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
        var nivel = CampaniaEstandar.crear(cargador::cargarDesdeRecurso).niveles().get(8);
        Partida partida = new Partida(java.util.List.of(nivel));
        long fragilesAntes = nivel.cajas().stream().filter(CajaFragil.class::isInstance).count();

        "DDDDDDBBBBDDDDDDDDDDBDAAIIIBIIIIIIIIIBBDDDDDDBDAAIADDDDDADBBIIIAIIIIIIIIIIIIIBBBDDADDDDAAAAAADB".chars().forEach(letra -> partida.mover(direccion((char) letra)));

        assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado());
        assertEquals(4, nivel.cajas().size());
        assertEquals(2, fragilesAntes);
        assertEquals(java.util.List.of(1, 2), nivel.cajas().stream().filter(CajaFragil.class::isInstance)
                .map(caja -> ((CajaFragil) caja).resistencia()).sorted().toList());
    }

    @Test
    void losNiveles5a7SeResuelvenConSuSolucionMinima() {
        CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
        var niveles = CampaniaEstandar.crear(cargador::cargarDesdeRecurso).niveles();

        SOLUCIONES.forEach((numero, pasos) -> {
            Partida partida = new Partida(java.util.List.of(niveles.get(numero - 1)));
            pasos.chars().forEach(letra -> partida.mover(direccion((char) letra)));
            assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado(), "nivel " + numero);
            assertEquals(pasos.length(), partida.nivelActual().movimientos(), "nivel " + numero);
        });
    }
}
