package sokoban.model.modo;

import java.time.Duration;
import sokoban.model.Nivel;
import sokoban.model.enums.EstadoPartida;
import sokoban.model.puntaje.EstadisticasDeNivel;

/**
 * Template Method: {@link #evaluar} fija el orden en que se decide qué pasó después de un movimiento
 * (primero se mira si el nivel está resuelto, y solo si no, si se agotó el límite del modo). Cada
 * modo completa los pasos que varían: el límite, el ajuste del puntaje y los textos.
 */
public abstract class ModoDeJuego {

    /** Paso fijo del algoritmo: completar el nivel siempre gana a perderlo, sea cual sea el modo. */
    public final EstadoPartida evaluar(Nivel nivel) {
        if (nivel.estaResuelto()) {
            return EstadoPartida.NIVEL_COMPLETADO;
        }
        if (limiteAgotado(nivel)) {
            return EstadoPartida.NIVEL_FALLIDO;
        }
        return EstadoPartida.JUGANDO;
    }

    /** Paso variable obligatorio: si el jugador ya no puede seguir en este nivel. */
    protected abstract boolean limiteAgotado(Nivel nivel);

    /** Nombre corto para mostrar y para guardar la partida. */
    public abstract String nombre();

    public abstract String descripcion();

    /** Gancho: por defecto el modo no cambia el puntaje de la estrategia de puntaje. */
    public int ajustarPuntaje(int puntajeBase, EstadisticasDeNivel estadisticas) {
        return puntajeBase;
    }

    /** Gancho: texto del reloj del HUD a partir del tiempo transcurrido; por defecto, el tiempo que va. */
    public String etiquetaDeTiempo(Duration transcurrido) {
        return "Tiempo " + comoMinutosYSegundos(transcurrido);
    }

    protected static String comoMinutosYSegundos(Duration duracion) {
        long segundos = Math.max(0, duracion.toSeconds());
        return String.format("%02d:%02d", segundos / 60, segundos % 60);
    }

    /** Gancho: cómo se muestra el contador de movimientos (los modos con tope lo muestran como 12/200). */
    public String etiquetaDeMovimientos(Nivel nivel) {
        return String.valueOf(nivel.movimientos());
    }
}
