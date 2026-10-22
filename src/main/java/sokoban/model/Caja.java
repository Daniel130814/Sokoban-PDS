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

    /** Punto de variación: qué hace la caja al quedar detenida sobre un cerrojo. Por defecto, nada. */
    public void alAsentarseEn(Cerrojo cerrojo) {
    }

    /** Una caja rota deja de existir en el nivel. Por defecto, las cajas no se rompen. */
    public boolean estaRota() {
        return false;
    }
}
