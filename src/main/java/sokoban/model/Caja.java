package sokoban.model;

public abstract class Caja {

    private Posicion posicion;

    protected Caja(Posicion posicionInicial) {
        this.posicion = posicionInicial;
    }

    public Posicion posicion() {
        return posicion;
    }

    void irA(Posicion nueva) {
        this.posicion = nueva;
    }

    /** Punto de variación: qué le pasa a cada tipo de caja cuando el jugador la empuja. */
    public abstract void alSerEmpujada();
}
