package sokoban.model.modo;

import sokoban.model.Nivel;

/** El juego de siempre: sin límites, el nivel solo termina cuando se resuelve. */
public class ModoClasico extends ModoDeJuego {

    @Override
    protected boolean limiteAgotado(Nivel nivel) {
        return false;
    }

    @Override
    public String nombre() {
        return "Clásico";
    }

    @Override
    public String descripcion() {
        return "Sin límites: resolvé cada nivel a tu ritmo";
    }
}
