package sokoban.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Reparte cada evento a todos los observadores registrados (lado Subject del Observer). */
public class DifusorDeEventos implements ObservadorDeJuego {

    private final List<ObservadorDeJuego> observadores = new ArrayList<>();
    private boolean silenciado;

    public void agregar(ObservadorDeJuego observador) {
        observadores.add(observador);
    }

    /** Mientras está silenciado no avisa a nadie (por ejemplo, al reproducir una partida guardada). */
    public void silenciar(boolean silenciado) {
        this.silenciado = silenciado;
    }

    private void difundir(Consumer<ObservadorDeJuego> evento) {
        if (!silenciado) {
            observadores.forEach(evento);
        }
    }

    @Override
    public void jugadorSeMovio() {
        difundir(ObservadorDeJuego::jugadorSeMovio);
    }

    @Override
    public void cajaEmpujada() {
        difundir(ObservadorDeJuego::cajaEmpujada);
    }

    @Override
    public void cajaSeRompio() {
        difundir(ObservadorDeJuego::cajaSeRompio);
    }

    @Override
    public void cerrojoActivado() {
        difundir(ObservadorDeJuego::cerrojoActivado);
    }

    @Override
    public void estadoRestaurado() {
        difundir(ObservadorDeJuego::estadoRestaurado);
    }

    @Override
    public void nivelCompletado() {
        difundir(ObservadorDeJuego::nivelCompletado);
    }

    @Override
    public void nivelCambiado() {
        difundir(ObservadorDeJuego::nivelCambiado);
    }

    @Override
    public void juegoTerminado() {
        difundir(ObservadorDeJuego::juegoTerminado);
    }
}
