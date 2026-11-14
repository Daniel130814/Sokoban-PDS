package sokoban.model.carga;

import sokoban.model.Caja;
import sokoban.model.CajaNormal;
import sokoban.model.Posicion;

/** Creador concreto: la caja común ('$' sobre piso, '*' sobre destino). */
public class CreadorDeCajaNormal implements CreadorDeCaja {

    @Override
    public Caja crearCaja(char simbolo, Posicion posicion) {
        return new CajaNormal(posicion);
    }
}
