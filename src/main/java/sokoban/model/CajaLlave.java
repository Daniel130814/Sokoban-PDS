package sokoban.model;

public class CajaLlave extends Caja {

    private final int id;

    public CajaLlave(Posicion posicionInicial, int id) {
        super(posicionInicial);
        this.id = id;
    }

    public int id() {
        return id;
    }

    @Override
    public void alSerEmpujada() {
        // Empujarla no tiene efecto propio: lo importante ocurre cuando se detiene sobre un cerrojo.
    }

    @Override
    public void alAsentarseEn(Cerrojo cerrojo) {
        if (cerrojo.id() == id) {
            cerrojo.activar();
        }
    }
}
