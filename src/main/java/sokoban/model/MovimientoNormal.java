package sokoban.model;

import java.util.function.Predicate;
import sokoban.model.enums.Direccion;

public class MovimientoNormal implements ReglaDeMovimiento {

    @Override
    public Posicion posicionFinal(Posicion llegada, Direccion direccion, Tablero tablero,
            Predicate<Posicion> ocupada) {
        return llegada;
    }
}
