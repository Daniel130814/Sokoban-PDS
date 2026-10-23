package sokoban.model;

import java.util.ArrayList;
import java.util.List;

/** Reparte cada evento a todos los observadores registrados (lado Subject del Observer). */
public class DifusorDeEventos implements ObservadorDeJuego {

    private final List<ObservadorDeJuego> observadores = new ArrayList<>();

    public void agregar(ObservadorDeJuego observador) {
        observadores.add(observador);
    }

    @Override
    public void jugadorSeMovio() {
        observadores.forEach(ObservadorDeJuego::jugadorSeMovio);
    }

    @Override
    public void cajaEmpujada() {
        observadores.forEach(ObservadorDeJuego::cajaEmpujada);
    }

    @Override
    public void cajaSeRompio() {
        observadores.forEach(ObservadorDeJuego::cajaSeRompio);
    }

    @Override
    public void cerrojoActivado() {
        observadores.forEach(ObservadorDeJuego::cerrojoActivado);
    }

    @Override
    public void estadoRestaurado() {
        observadores.forEach(ObservadorDeJuego::estadoRestaurado);
    }

    @Override
    public void nivelCompletado() {
        observadores.forEach(ObservadorDeJuego::nivelCompletado);
    }

    @Override
    public void nivelCambiado() {
        observadores.forEach(ObservadorDeJuego::nivelCambiado);
    }

    @Override
    public void juegoTerminado() {
        observadores.forEach(ObservadorDeJuego::juegoTerminado);
    }
}
