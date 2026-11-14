package sokoban.model.carga;

import sokoban.model.Caja;
import sokoban.model.Posicion;

/**
 * Creator del Factory Method: cada implementación decide qué tipo de {@link Caja} fabricar para el
 * símbolo del archivo de nivel. Sumar un tipo de caja es escribir un creador nuevo y registrarlo.
 */
public interface CreadorDeCaja {

    Caja crearCaja(char simbolo, Posicion posicion);
}
