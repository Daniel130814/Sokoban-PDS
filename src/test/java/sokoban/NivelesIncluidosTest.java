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
            "/levels/nivel1.txt", "/levels/nivel2.txt", "/levels/nivel3.txt", "/levels/nivel4.txt",
            "/levels/nivel5.txt");

    private static final Map<Integer, List<Direccion>> SOLUCIONES = Map.of(
            0, List.of(Direccion.IZQUIERDA),
            1, repetir(Direccion.DERECHA, 5),
            2, repetir(Direccion.DERECHA, 6),
            3, repetir(Direccion.DERECHA, 3),
            4, bancoDePruebas());

    /** Nivel 5: un carril por cada tipo de caja (normal, frágil, hielo y llave con muro). */
    private static List<Direccion> bancoDePruebas() {
        List<Direccion> pasos = new java.util.ArrayList<>();
        for (Object[] tramo : new Object[][] {
                {Direccion.DERECHA, 7}, {Direccion.IZQUIERDA, 6}, {Direccion.ABAJO, 2},
                {Direccion.DERECHA, 2}, {Direccion.IZQUIERDA, 2}, {Direccion.ABAJO, 2},
                {Direccion.DERECHA, 1}, {Direccion.IZQUIERDA, 1}, {Direccion.ABAJO, 2},
                {Direccion.DERECHA, 6}}) {
            pasos.addAll(repetir((Direccion) tramo[0], (int) tramo[1]));
        }
        return pasos;
    }

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
