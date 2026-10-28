package sokoban.view;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.Timer;

/** Parpadeo breve: una capa de color que se desvanece en unos pocos cuadros de un {@link Timer}. */
class EfectoDeDestello {

    static final int CUADROS = 6;
    private static final int MILISEGUNDOS_POR_CUADRO = 40;
    private static final int ALFA_MAXIMO = 110;

    private final Timer temporizador;
    private Color color = Color.WHITE;
    private int restantes;

    EfectoDeDestello(Runnable repintar) {
        temporizador = new Timer(MILISEGUNDOS_POR_CUADRO, evento -> {
            paso();
            repintar.run();
        });
    }

    void disparar(Color nuevoColor) {
        color = nuevoColor;
        restantes = CUADROS;
        temporizador.restart();
    }

    void paso() {
        if (restantes > 0) {
            restantes--;
        }
        if (restantes == 0) {
            temporizador.stop();
        }
    }

    boolean activo() {
        return restantes > 0;
    }

    void dibujar(Graphics2D g, int ancho, int alto) {
        if (restantes == 0) {
            return;
        }
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ALFA_MAXIMO * restantes / CUADROS));
        g.fillRect(0, 0, ancho, alto);
    }
}
