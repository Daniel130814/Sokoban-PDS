package sokoban.model.modo;

import sokoban.model.Nivel;

/**
 * Cada nivel tiene un tope de movimientos. Cuenta los movimientos del nivel tal como están ahora:
 * deshacer los devuelve y reiniciar el nivel los pone en cero.
 */
public class ModoMovimientosLimitados extends ModoDeJuego {

    public static final int MAXIMO_POR_DEFECTO = 200;

    private final int maximo;

    public ModoMovimientosLimitados() {
        this(MAXIMO_POR_DEFECTO);
    }

    public ModoMovimientosLimitados(int maximo) {
        if (maximo < 1) {
            throw new IllegalArgumentException("El máximo de movimientos debe ser positivo");
        }
        this.maximo = maximo;
    }

    @Override
    protected boolean limiteAgotado(Nivel nivel) {
        return nivel.movimientos() >= maximo;
    }

    @Override
    public String nombre() {
        return "Movimientos limitados";
    }

    @Override
    public String descripcion() {
        return "Tenés " + maximo + " movimientos por nivel";
    }

    @Override
    public String etiquetaDeMovimientos(Nivel nivel) {
        return nivel.movimientos() + "/" + maximo;
    }
}
