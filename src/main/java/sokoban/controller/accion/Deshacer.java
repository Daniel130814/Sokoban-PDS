package sokoban.controller.accion;

import sokoban.model.Partida;

public record Deshacer() implements Accion {

    @Override
    public boolean ejecutar(Partida partida) {
        return partida.deshacer();
    }

    @Override
    public String comoTexto() {
        return "DESHACER";
    }
}
