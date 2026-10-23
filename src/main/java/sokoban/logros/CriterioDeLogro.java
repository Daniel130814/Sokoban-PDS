package sokoban.logros;

/** Strategy: cada logro decide con su propio criterio si se cumple. */
@FunctionalInterface
public interface CriterioDeLogro {

    boolean seCumple(EstadisticasDeJuego estadisticas);
}
