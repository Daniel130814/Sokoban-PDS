package sokoban.model;

import java.util.List;
import java.util.function.Consumer;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;
import sokoban.model.puntaje.EstadisticasDeNivel;
import sokoban.model.puntaje.EstrategiaDePuntaje;
import sokoban.model.puntaje.PuntajePorEficiencia;
import sokoban.model.puntaje.ResumenDeNivel;

public class Partida {

    private final List<Nivel> niveles;
    private int indiceActual;
    private final DifusorDeEventos observadores = new DifusorDeEventos();
    private final EstrategiaDePuntaje estrategiaDePuntaje;
    private final List<EstadoNivel> estadosIniciales;
    private SesionDeNivel sesion;
    private EstadoPartida estado = EstadoPartida.JUGANDO;

    public Partida(List<Nivel> niveles) {
        this(niveles, new PuntajePorEficiencia());
    }

    public Partida(List<Nivel> niveles, EstrategiaDePuntaje estrategiaDePuntaje) {
        if (niveles.isEmpty()) {
            throw new IllegalArgumentException("Una partida necesita al menos un nivel");
        }
        this.estrategiaDePuntaje = estrategiaDePuntaje;
        this.niveles = List.copyOf(niveles);
        this.niveles.forEach(nivel -> nivel.agregarObservador(observadores));
        this.estadosIniciales = this.niveles.stream().map(Nivel::guardarEstado).toList();
        this.sesion = new SesionDeNivel(niveles.get(0));
    }

    /**
     * Deja todos los niveles como al empezar, ejecuta las acciones dadas sin avisar a los observadores y
     * después avisa una sola vez que el nivel cambió, para que la vista se refresque.
     */
    public void reconstruir(Consumer<Partida> acciones) {
        observadores.silenciar(true);
        try {
            for (int i = 0; i < niveles.size(); i++) {
                niveles.get(i).restaurar(estadosIniciales.get(i));
            }
            indiceActual = 0;
            estado = EstadoPartida.JUGANDO;
            sesion = new SesionDeNivel(niveles.get(0));
            acciones.accept(this);
        } finally {
            observadores.silenciar(false);
        }
        observadores.nivelCambiado();
    }

    public void agregarObservador(ObservadorDeJuego observador) {
        observadores.agregar(observador);
    }

    public EstadoPartida estado() {
        return estado;
    }

    public Nivel nivelActual() {
        return sesion.nivel();
    }

    public int numeroDeNivel() {
        return indiceActual + 1;
    }

    public int usosDeDeshacer() {
        return sesion.usosDeDeshacer();
    }

    /** Context del Strategy: arma las estadísticas del nivel y delega el cálculo del puntaje. */
    public ResumenDeNivel resumenDelNivel() {
        if (estado == EstadoPartida.JUGANDO) {
            throw new IllegalStateException("El nivel todavía no está completado");
        }
        EstadisticasDeNivel estadisticas = new EstadisticasDeNivel(
                nivelActual().movimientos(), nivelActual().empujes(), sesion.usosDeDeshacer());
        return new ResumenDeNivel(estadisticas, estrategiaDePuntaje.calcular(estadisticas));
    }

    public boolean mover(Direccion direccion) {
        if (estado != EstadoPartida.JUGANDO) {
            return false;
        }
        boolean seMovio = sesion.mover(direccion);
        if (seMovio && nivelActual().estaResuelto()) {
            estado = EstadoPartida.NIVEL_COMPLETADO;
            observadores.nivelCompletado();
        }
        return seMovio;
    }

    public boolean deshacer() {
        return estado == EstadoPartida.JUGANDO && sesion.deshacer();
    }

    public void reiniciarNivel() {
        if (estado == EstadoPartida.JUGANDO) {
            sesion.reiniciar();
        }
    }

    public void avanzarDeNivel() {
        if (estado != EstadoPartida.NIVEL_COMPLETADO) {
            throw new IllegalStateException("Solo se puede avanzar con el nivel completado (estado: " + estado + ")");
        }
        if (indiceActual + 1 < niveles.size()) {
            indiceActual++;
            sesion = new SesionDeNivel(niveles.get(indiceActual));
            estado = EstadoPartida.JUGANDO;
            observadores.nivelCambiado();
        } else {
            estado = EstadoPartida.JUEGO_TERMINADO;
            observadores.juegoTerminado();
        }
    }
}
