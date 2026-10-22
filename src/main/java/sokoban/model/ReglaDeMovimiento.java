package sokoban.model;

import java.util.function.Predicate;
import sokoban.model.enums.Direccion;

/** Strategy: define dónde termina una caja que acaba de llegar a una celda al ser empujada. */
public interface ReglaDeMovimiento {

    Posicion posicionFinal(Posicion llegada, Direccion direccion, Tablero tablero, Predicate<Posicion> ocupada);
}
