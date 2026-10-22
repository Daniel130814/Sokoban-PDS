package sokoban.model;

import java.util.List;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

public class Partida {

    private final List<Nivel> niveles;
    private int indiceActual;
    private EstadoPartida estado = EstadoPartida.JUGANDO;

    public Partida(List<Nivel> niveles) {
        if (niveles.isEmpty()) {
            throw new IllegalArgumentException("Una partida necesita al menos un nivel");
        }
        this.niveles = List.copyOf(niveles);
    }

    public EstadoPartida estado() {
        return estado;
    }

    public Nivel nivelActual() {
        return niveles.get(indiceActual);
    }

    public int numeroDeNivel() {
        return indiceActual + 1;
    }

    public boolean mover(Direccion direccion) {
        if (estado != EstadoPartida.JUGANDO) {
            return false;
        }
        boolean seMovio = nivelActual().moverJugador(direccion);
        if (seMovio && nivelActual().estaResuelto()) {
            estado = EstadoPartida.NIVEL_COMPLETADO;
        }
        return seMovio;
    }

    public void avanzarDeNivel() {
        if (estado != EstadoPartida.NIVEL_COMPLETADO) {
            throw new IllegalStateException("Solo se puede avanzar con el nivel completado (estado: " + estado + ")");
        }
        if (indiceActual + 1 < niveles.size()) {
            indiceActual++;
            estado = EstadoPartida.JUGANDO;
        } else {
            estado = EstadoPartida.JUEGO_TERMINADO;
        }
    }
}
