package sokoban.model;

public class CajaNormal extends Caja {

    public CajaNormal(Posicion posicionInicial) {
        super(posicionInicial);
    }

    @Override
    public void alSerEmpujada() {
        // La caja normal no tiene efecto adicional al ser empujada.
    }
}
