package sokoban.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import sokoban.controller.ControladorDeJuego;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;
import sokoban.model.Tablero;
import sokoban.model.enums.Direccion;

/** Vista del tablero: dibuja el estado actual del nivel y reenvía las flechas al controlador. */
public class PanelDeTablero extends PanelEspacial implements ObservadorDeJuego {

    static final int TAMANIO_CELDA = 48;
    private static final Color DESTELLO_ROTURA = new Color(255, 70, 50);
    private static final Color DESTELLO_COMPUERTA = new Color(60, 220, 255);

    private final Partida partida;
    private final DibujanteDeNivel dibujante;
    private final EfectoDeDestello destello = new EfectoDeDestello(this::repaint);
    private Direccion orientacion = Direccion.ARRIBA;

    public PanelDeTablero(Partida partida, ControladorDeJuego controlador, RepositorioDeImagenes imagenes) {
        super(imagenes, "fondo_espacio");
        this.partida = partida;
        this.dibujante = new DibujanteDeNivel(imagenes);
        setFocusable(true);
        asociarTecla(KeyEvent.VK_UP, Direccion.ARRIBA, controlador);
        asociarTecla(KeyEvent.VK_DOWN, Direccion.ABAJO, controlador);
        asociarTecla(KeyEvent.VK_LEFT, Direccion.IZQUIERDA, controlador);
        asociarTecla(KeyEvent.VK_RIGHT, Direccion.DERECHA, controlador);
        actualizarTamanio();
    }

    private void asociarTecla(int tecla, Direccion direccion, ControladorDeJuego controlador) {
        String nombre = "mover-" + direccion;
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla, 0), nombre);
        getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                if (!isShowing()) {
                    return;
                }
                orientacion = direccion;
                controlador.mover(direccion);
                repaint();
            }
        });
    }

    private void actualizarTamanio() {
        Tablero tablero = partida.nivelActual().tablero();
        setPreferredSize(new Dimension(tablero.ancho() * TAMANIO_CELDA, tablero.alto() * TAMANIO_CELDA));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujar((Graphics2D) g);
        destello.dibujar((Graphics2D) g, getWidth(), getHeight());
    }

    void dibujar(Graphics2D g) {
        dibujante.dibujar(g, partida.nivelActual(), orientacion);
    }

    @Override
    public void jugadorSeMovio() {
        repaint();
    }

    @Override
    public void cajaSeRompio() {
        destello.disparar(DESTELLO_ROTURA);
    }

    @Override
    public void cerrojoActivado() {
        destello.disparar(DESTELLO_COMPUERTA);
    }

    @Override
    public void estadoRestaurado() {
        repaint();
    }

    @Override
    public void nivelCambiado() {
        actualizarTamanio();
        repaint();
    }
}
