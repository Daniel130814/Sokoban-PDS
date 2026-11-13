package sokoban.tiempo;

import java.time.Duration;
import java.util.function.LongSupplier;
import sokoban.model.ObservadorDeJuego;

/**
 * Observer concreto: mide cuánto tarda el jugador en resolver cada nivel. Arranca con el primer
 * movimiento, se detiene al completar el nivel y vuelve a cero cuando cambia el nivel.
 * El juego no sabe que existe.
 */
public class Cronometro implements ObservadorDeJuego {

    private static final long SIN_MARCA = -1;

    private final LongSupplier relojEnMilisegundos;
    private long inicio = SIN_MARCA;
    private long fin = SIN_MARCA;

    public Cronometro() {
        this(System::currentTimeMillis);
    }

    public Cronometro(LongSupplier relojEnMilisegundos) {
        this.relojEnMilisegundos = relojEnMilisegundos;
    }

    public Duration transcurrido() {
        if (inicio == SIN_MARCA) {
            return Duration.ZERO;
        }
        long hasta = fin != SIN_MARCA ? fin : relojEnMilisegundos.getAsLong();
        return Duration.ofMillis(hasta - inicio);
    }

    /** Tiempo como mm:ss, listo para mostrar. */
    public String comoTexto() {
        long segundos = transcurrido().toSeconds();
        return String.format("%02d:%02d", segundos / 60, segundos % 60);
    }

    @Override
    public void jugadorSeMovio() {
        if (inicio == SIN_MARCA) {
            inicio = relojEnMilisegundos.getAsLong();
        }
    }

    @Override
    public void nivelCompletado() {
        if (inicio != SIN_MARCA && fin == SIN_MARCA) {
            fin = relojEnMilisegundos.getAsLong();
        }
    }

    @Override
    public void nivelCambiado() {
        inicio = SIN_MARCA;
        fin = SIN_MARCA;
    }
}