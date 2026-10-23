package sokoban.model;

/**
 * Observer: la vista, el HUD y el sonido se enteran de lo que pasa en el modelo sin que este los
 * conozca. Cada observador implementa solo los eventos que le interesan.
 */
public interface ObservadorDeJuego {

    default void jugadorSeMovio() {
    }

    default void cajaEmpujada() {
    }

    default void cajaSeRompio() {
    }

    default void cerrojoActivado() {
    }

    default void estadoRestaurado() {
    }

    default void nivelCompletado() {
    }

    default void nivelCambiado() {
    }

    default void juegoTerminado() {
    }
}
