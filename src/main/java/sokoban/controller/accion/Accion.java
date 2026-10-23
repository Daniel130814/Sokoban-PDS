package sokoban.controller.accion;

import sokoban.model.Partida;

/** Command: una acción del jugador como objeto, que se puede ejecutar, registrar, guardar y reproducir. */
public interface Accion {

    /** Ejecuta la acción sobre la partida y dice si tuvo algún efecto. */
    boolean ejecutar(Partida partida);

    /** Forma textual de la acción, usada al guardar la partida. */
    String comoTexto();
}
