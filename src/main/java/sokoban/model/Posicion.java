package sokoban.model;

import sokoban.model.enums.Direccion;

public record Posicion(int fila, int columna) {

    public Posicion desplazar(Direccion direccion) {
        return new Posicion(fila + direccion.deltaFila(), columna + direccion.deltaColumna());
    }
}
