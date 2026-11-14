package sokoban.tiempo;

import java.time.Duration;
import sokoban.model.Nivel;
import sokoban.model.modo.ModoDeJuego;
import sokoban.model.puntaje.EstadisticasDeNivel;

/**
 * Cada nivel tiene un tiempo límite medido por el {@link Cronometro}, que arranca con el primer
 * movimiento. El tiempo se revisa al mover: si el jugador se queda quieto, pierde en su próxima jugada.
 * Vive en el paquete {@code tiempo} para que {@code model} no dependa del cronómetro.
 */
public class ModoContrarreloj extends ModoDeJuego {

    public static final Duration LIMITE_POR_DEFECTO = Duration.ofSeconds(180);

    private final Cronometro cronometro;
    private final Duration limite;

    public ModoContrarreloj(Cronometro cronometro) {
        this(cronometro, LIMITE_POR_DEFECTO);
    }

    public ModoContrarreloj(Cronometro cronometro, Duration limite) {
        this.cronometro = cronometro;
        this.limite = limite;
    }

    @Override
    protected boolean limiteAgotado(Nivel nivel) {
        return cronometro.transcurrido().compareTo(limite) > 0;
    }

    /** Premia terminar rápido: un punto por cada segundo que sobró. */
    @Override
    public int ajustarPuntaje(int puntajeBase, EstadisticasDeNivel estadisticas) {
        Duration sobrante = limite.minus(cronometro.transcurrido());
        return puntajeBase + (int) Math.max(0, sobrante.toSeconds());
    }

    /** En este modo lo que importa es cuánto queda, no cuánto pasó. */
    @Override
    public String etiquetaDeTiempo(Duration transcurrido) {
        return "Quedan " + comoMinutosYSegundos(limite.minus(transcurrido));
    }

    @Override
    public String nombre() {
        return "Contrarreloj";
    }

    @Override
    public String descripcion() {
        return "Resolvé cada nivel en " + limite.toSeconds() + " segundos";
    }
}
