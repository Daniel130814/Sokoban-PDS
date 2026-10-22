package sokoban.model;

import sokoban.model.enums.Direccion;

public class Jugador {

    private Posicion posicion;

    public Jugador(Posicion posicionInicial) {
        this.posicion = posicionInicial;
    }

    public Posicion posicion() {
        return posicion;
    }

    public boolean mover(Direccion direccion, Tablero tablero) {
        Posicion destino = posicion.desplazar(direccion);
        if (!tablero.esTransitable(destino)) {
            return false;
        }
        posicion = destino;
        return true;
    }
}
