package sokoban.model.carga;

import java.util.OptionalInt;
import sokoban.model.Caja;
import sokoban.model.CajaLlave;
import sokoban.model.Posicion;

/** Creador concreto: la caja llave ('a' a 'i'); la letra indica el id del cerrojo que abre (1 a 9). */
public class CreadorDeCajaLlave implements CreadorDeCaja {

    /** Id de llave para el símbolo, o vacío si no es una caja llave. */
    public static OptionalInt idDe(char simbolo) {
        return simbolo >= 'a' && simbolo <= 'i' ? OptionalInt.of(simbolo - 'a' + 1) : OptionalInt.empty();
    }

    @Override
    public Caja crearCaja(char simbolo, Posicion posicion) {
        int id = idDe(simbolo).orElseThrow(() -> new IllegalArgumentException("No es una caja llave: " + simbolo));
        return new CajaLlave(posicion, id);
    }
}
