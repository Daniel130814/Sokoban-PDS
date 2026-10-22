package sokoban.model;

public class CajaFragil extends Caja {

    public static final int RESISTENCIA_INICIAL = 3;

    private int resistencia;

    public CajaFragil(Posicion posicionInicial) {
        this(posicionInicial, RESISTENCIA_INICIAL);
    }

    public CajaFragil(Posicion posicionInicial, int resistencia) {
        super(posicionInicial);
        if (resistencia < 1) {
            throw new IllegalArgumentException("La resistencia inicial debe ser al menos 1");
        }
        this.resistencia = resistencia;
    }

    public int resistencia() {
        return resistencia;
    }

    @Override
    public void alSerEmpujada() {
        resistencia--;
    }

    @Override
    public boolean estaRota() {
        return resistencia <= 0;
    }
}
