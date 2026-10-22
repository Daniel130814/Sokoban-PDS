package sokoban.model;

import java.util.function.Predicate;
import sokoban.model.enums.Direccion;

/**
 * La caja sigue deslizándose hasta chocar con una pared u otra caja, o hasta llegar a una celda
 * cuya propia regla la detenga (piso, destino).
 */
public class Deslizamiento implements ReglaDeMovimiento {

    @Override
    public Posicion posicionFinal(Posicion llegada, Direccion direccion, Tablero tablero,
            Predicate<Posicion> ocupada) {
        Posicion siguiente = llegada.desplazar(direccion);
        if (!tablero.esTransitable(siguiente) || ocupada.test(siguiente)) {
            return llegada;
        }
        return tablero.reglaEn(siguiente).posicionFinal(siguiente, direccion, tablero, ocupada);
    }
}
