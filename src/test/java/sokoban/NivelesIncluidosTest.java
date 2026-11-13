package sokoban;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import sokoban.controller.ControladorDeJuego;
import sokoban.model.Nivel;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

/** Prueba de aceptación: cada nivel incluido tiene una solución conocida. */
class NivelesIncluidosTest {

    private static final List<String> NIVELES = List.of(
            "/levels/nivel1.txt", "/levels/nivel2.txt", "/levels/nivel3.txt", "/levels/nivel4.txt");

    private static final Map<Integer, List<Direccion>> SOLUCIONES = Map.of(
            0, List.of(Direccion.IZQUIERDA),
            1, repetir(Direccion.DERECHA, 5),
            2, repetir(Direccion.DERECHA, 6),
            3, repetir(Direccion.DERECHA, 3));

    private static List<Direccion> repetir(Direccion direccion, int veces) {
        return java.util.Collections.nCopies(veces, direccion);
    }

    @Test
    void todosLosNivelesSeResuelvenYElJuegoTermina() {
        CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
        List<Nivel> niveles = NIVELES.stream().map(cargador::cargarDesdeRecurso).toList();
        Partida partida = new Partida(niveles);
        ControladorDeJuego controlador = new ControladorDeJuego(partida);

        for (int i = 0; i < NIVELES.size(); i++) {
            SOLUCIONES.get(i).forEach(controlador::mover);
            assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado(), "nivel " + (i + 1));
            controlador.continuar();
        }

        assertEquals(EstadoPartida.JUEGO_TERMINADO, partida.estado());
    }
}
