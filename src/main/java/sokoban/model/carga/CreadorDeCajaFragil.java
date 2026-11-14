package sokoban.model.carga;

import sokoban.model.Caja;
import sokoban.model.CajaFragil;
import sokoban.model.Posicion;

/** Creador concreto: la caja frágil ('%'). */
public class CreadorDeCajaFragil implements CreadorDeCaja {

    @Override
    public Caja crearCaja(char simbolo, Posicion posicion) {
        return new CajaFragil(posicion);
    }
}
