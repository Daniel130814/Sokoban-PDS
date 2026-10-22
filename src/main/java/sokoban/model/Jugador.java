package sokoban.model;

public class Jugador {

    private Posicion posicion;

    public Jugador(Posicion posicionInicial) {
        this.posicion = posicionInicial;
    }

    public Posicion posicion() {
        return posicion;
    }

    void irA(Posicion nueva) {
        this.posicion = nueva;
    }
}
