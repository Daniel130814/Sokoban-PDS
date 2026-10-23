package sokoban.controller;

import sokoban.model.Partida;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

/**
 * GRASP Controller: traduce las acciones del usuario (teclas, botones) en operaciones del modelo.
 * No depende de Swing, por lo que se puede probar sin interfaz.
 */
public class ControladorDeJuego {

    private final Partida partida;

    public ControladorDeJuego(Partida partida) {
        this.partida = partida;
    }

    public void mover(Direccion direccion) {
        partida.mover(direccion);
    }

    public void deshacer() {
        partida.deshacer();
    }

    public void reiniciarNivel() {
        partida.reiniciarNivel();
    }

    public void continuar() {
        if (partida.estado() == EstadoPartida.NIVEL_COMPLETADO) {
            partida.avanzarDeNivel();
        }
    }
}
