package sokoban.controller.accion;

import sokoban.model.Partida;
import sokoban.model.enums.EstadoPartida;

public record Continuar() implements Accion {

    @Override
    public boolean ejecutar(Partida partida) {
        if (partida.estado() != EstadoPartida.NIVEL_COMPLETADO) {
            return false;
        }
        partida.avanzarDeNivel();
        return true;
    }

    @Override
    public String comoTexto() {
        return "CONTINUAR";
    }
}
