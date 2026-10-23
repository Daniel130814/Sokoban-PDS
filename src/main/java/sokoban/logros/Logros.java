package sokoban.logros;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;

/**
 * Observer concreto: escucha los eventos del juego, acumula estadísticas y desbloquea los logros
 * cuyo criterio (Strategy) se cumple. No participa del modelo: el juego no sabe que existe.
 */
public class Logros implements ObservadorDeJuego {

    private final Partida partida;
    private final List<Logro> catalogo;
    private final List<Logro> desbloqueados = new ArrayList<>();
    private final List<Consumer<Logro>> oyentes = new ArrayList<>();
    private final EstadisticasDeJuego estadisticas = new EstadisticasDeJuego();

    public Logros(Partida partida, List<Logro> catalogo) {
        this.partida = partida;
        this.catalogo = List.copyOf(catalogo);
    }

    public void alDesbloquear(Consumer<Logro> oyente) {
        oyentes.add(oyente);
    }

    public List<Logro> desbloqueados() {
        return List.copyOf(desbloqueados);
    }

    public int total() {
        return catalogo.size();
    }

    public EstadisticasDeJuego estadisticas() {
        return estadisticas;
    }

    @Override
    public void cajaEmpujada() {
        estadisticas.registrarEmpuje();
        evaluar();
    }

    @Override
    public void cajaSeRompio() {
        estadisticas.registrarCajaRota();
        evaluar();
    }

    @Override
    public void cerrojoActivado() {
        estadisticas.registrarCerrojoActivado();
        evaluar();
    }

    @Override
    public void nivelCompletado() {
        boolean sinDeshacer = partida.resumenDelNivel().estadisticas().usosDeDeshacer() == 0;
        estadisticas.registrarNivelCompletado(sinDeshacer);
        evaluar();
    }

    private void evaluar() {
        for (Logro logro : catalogo) {
            if (!desbloqueados.contains(logro) && logro.criterio().seCumple(estadisticas)) {
                desbloqueados.add(logro);
                oyentes.forEach(oyente -> oyente.accept(logro));
            }
        }
    }
}
