package sokoban.model;

import java.util.List;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;

public class Partida {

    private final List<Nivel> niveles;
    private int indiceActual;
    private SesionDeNivel sesion;
    private EstadoPartida estado = EstadoPartida.JUGANDO;

    public Partida(List<Nivel> niveles) {
        if (niveles.isEmpty()) {
            throw new IllegalArgumentException("Una partida necesita al menos un nivel");
        }
        this.niveles = List.copyOf(niveles);
        this.sesion = new SesionDeNivel(niveles.get(0));
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

    public boolean mover(Direccion direccion) {
        if (estado != EstadoPartida.JUGANDO) {
            return false;
        }
        boolean seMovio = sesion.mover(direccion);
        if (seMovio && nivelActual().estaResuelto()) {
            estado = EstadoPartida.NIVEL_COMPLETADO;
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
        } else {
            estado = EstadoPartida.JUEGO_TERMINADO;
        }
    }
}
