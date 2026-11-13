package sokoban.logros;

import java.util.function.ToIntFunction;

/**
 * Un logro: su criterio (Strategy) decide si se cumple, y {@code medida} / {@code meta} dicen cuánto
 * falta para lograrlo (para mostrar una barra de progreso).
 */
public record Logro(String nombre, String descripcion, CriterioDeLogro criterio, int meta,
        ToIntFunction<EstadisticasDeJuego> medida) {

    /** Logro de todo o nada: el progreso es 0 hasta que el criterio se cumple. */
    public Logro(String nombre, String descripcion, CriterioDeLogro criterio) {
        this(nombre, descripcion, criterio, 1, estadisticas -> criterio.seCumple(estadisticas) ? 1 : 0);
    }

    /** Logro que se cumple cuando un contador llega a la meta. */
    public static Logro porContador(String nombre, String descripcion, int meta,
            ToIntFunction<EstadisticasDeJuego> contador) {
        return new Logro(nombre, descripcion, estadisticas -> contador.applyAsInt(estadisticas) >= meta, meta,
                contador);
    }

    public int avance(EstadisticasDeJuego estadisticas) {
        return Math.min(meta, medida.applyAsInt(estadisticas));
    }
}
