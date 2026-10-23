package sokoban.controller.accion;

import sokoban.model.Partida;
import sokoban.model.enums.EstadoPartida;

public record Reiniciar() implements Accion {

    @Override
    public boolean ejecutar(Partida partida) {
        if (partida.estado() != EstadoPartida.JUGANDO) {
            return false;
        }
        partida.reiniciarNivel();
        return true;
    }

    @Override
    public String comoTexto() {
        return "REINICIAR";
    }
}
