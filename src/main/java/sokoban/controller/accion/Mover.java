package sokoban.controller.accion;

import sokoban.model.Partida;
import sokoban.model.enums.Direccion;

public record Mover(Direccion direccion) implements Accion {

    @Override
    public boolean ejecutar(Partida partida) {
        return partida.mover(direccion);
    }

    @Override
    public String comoTexto() {
        return "MOVER " + direccion;
    }
}
