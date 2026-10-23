package sokoban.audio;

import sokoban.model.ObservadorDeJuego;

/** Observer concreto: traduce eventos del modelo en efectos de sonido. */
public class SonidosDelJuego implements ObservadorDeJuego {

    private final ReproductorDeSonido reproductor;

    public SonidosDelJuego(ReproductorDeSonido reproductor) {
        this.reproductor = reproductor;
    }

    @Override
    public void jugadorSeMovio() {
        reproductor.reproducir(Efecto.PASO);
    }

    @Override
    public void cajaEmpujada() {
        reproductor.reproducir(Efecto.EMPUJE);
    }

    @Override
    public void cajaSeRompio() {
        reproductor.reproducir(Efecto.CAJA_ROTA);
    }

    @Override
    public void cerrojoActivado() {
        reproductor.reproducir(Efecto.CERROJO);
    }

    @Override
    public void estadoRestaurado() {
        reproductor.reproducir(Efecto.DESHACER);
    }

    @Override
    public void nivelCompletado() {
        reproductor.reproducir(Efecto.NIVEL_COMPLETADO);
    }
}
