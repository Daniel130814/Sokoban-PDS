package sokoban.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import sokoban.controller.ControladorDeJuego;
import sokoban.model.Caja;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;
import sokoban.model.Posicion;
import sokoban.model.Tablero;
import sokoban.model.TipoCelda;
import sokoban.model.enums.Direccion;

/** Vista del tablero: dibuja el estado actual del nivel y reenvía las flechas al controlador. */
public class PanelDeTablero extends PanelEspacial implements ObservadorDeJuego {

    static final int TAMANIO_CELDA = 48;
    private static final Color DESTELLO_ROTURA = new Color(255, 70, 50);
    private static final Color DESTELLO_COMPUERTA = new Color(60, 220, 255);
    private static final Color CAJA_EN_DESTINO = new Color(0, 200, 0, 90);

    private final Partida partida;
    private final RepositorioDeImagenes imagenes;
    private final SelectorDeSprites sprites = new SelectorDeSprites();
    private final EfectoDeDestello destello = new EfectoDeDestello(this::repaint);
    private Direccion orientacion = Direccion.ARRIBA;

    public PanelDeTablero(Partida partida, ControladorDeJuego controlador, RepositorioDeImagenes imagenes) {
        super(imagenes, "fondo_espacio");
        this.partida = partida;
        this.imagenes = imagenes;
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
        Tablero tablero = partida.nivelActual().tablero();
        for (int fila = 0; fila < tablero.alto(); fila++) {
            for (int columna = 0; columna < tablero.ancho(); columna++) {
                TipoCelda celda = tablero.celdaEn(new Posicion(fila, columna));
                dibujarSprite(g, sprites.paraCelda(celda), new Posicion(fila, columna));
            }
        }
        for (Caja caja : partida.nivelActual().cajas()) {
            dibujarSprite(g, sprites.paraCaja(caja), caja.posicion());
            if (tablero.celdaEn(caja.posicion()) == TipoCelda.DESTINO) {
                g.setColor(CAJA_EN_DESTINO);
                g.fillRect(caja.posicion().columna() * TAMANIO_CELDA, caja.posicion().fila() * TAMANIO_CELDA,
                        TAMANIO_CELDA, TAMANIO_CELDA);
            }
        }
        dibujarJugador(g, partida.nivelActual().jugador().posicion());
    }

    /** El sprite del astronauta mira hacia arriba; se rota según la última dirección pedida. */
    private void dibujarJugador(Graphics2D g, Posicion posicion) {
        double grados = switch (orientacion) {
            case ARRIBA -> 0;
            case DERECHA -> 90;
            case ABAJO -> 180;
            case IZQUIERDA -> 270;
        };
        AffineTransform original = g.getTransform();
        g.rotate(Math.toRadians(grados), (posicion.columna() + 0.5) * TAMANIO_CELDA,
                (posicion.fila() + 0.5) * TAMANIO_CELDA);
        dibujarSprite(g, sprites.paraJugador(), posicion);
        g.setTransform(original);
    }

    private void dibujarSprite(Graphics2D g, String nombre, Posicion posicion) {
        g.drawImage(imagenes.imagen(nombre), posicion.columna() * TAMANIO_CELDA, posicion.fila() * TAMANIO_CELDA,
                TAMANIO_CELDA, TAMANIO_CELDA, null);
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
