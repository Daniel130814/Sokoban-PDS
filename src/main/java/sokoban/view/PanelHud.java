package sokoban.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sokoban.controller.ControladorDeJuego;
import sokoban.exception.GuardadoInvalidoException;
import sokoban.logros.Logro;
import sokoban.logros.Logros;
import sokoban.model.Nivel;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;

/**
 * HUD dibujado a mano sobre un panel semitransparente: nivel, movimientos, empujes, deshacer y último
 * puntaje con su ícono, más los botones de deshacer, reiniciar, guardar y cargar y los logros.
 */
public class PanelHud extends JPanel implements ObservadorDeJuego {

    static final Path RUTA_DE_GUARDADO = Path.of("partida-guardada.txt");

    private static final Color VELO = new Color(8, 10, 28, 175);
    private static final Color BORDE = new Color(120, 170, 255, 120);
    private static final int ALTO_DE_DATOS = 40;
    private static final int ANCHO_MINIMO = 660;
    private static final int LADO_DE_ICONO = 24;
    private static final int CANTIDAD_DE_DATOS = 5;

    private final Partida partida;
    private final Logros logros;
    private final ControladorDeJuego controlador;
    private final RepositorioDeImagenes imagenes;
    private final Font fuenteDeDatos;
    private final JLabel logrosObtenidos = new JLabel();
    private String nivel = "";
    private String movimientos = "";
    private String empujes = "";
    private String deshacer = "";
    private String puntaje = "-";

    public PanelHud(Partida partida, ControladorDeJuego controlador, Logros logros,
            RepositorioDeImagenes imagenes, RepositorioDeFuentes fuentes) {
        super(new BorderLayout());
        this.partida = partida;
        this.controlador = controlador;
        this.logros = logros;
        this.imagenes = imagenes;
        this.fuenteDeDatos = fuentes.fuente("kenvector_future_thin", 12f);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(ALTO_DE_DATOS, 10, 8, 10));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(boton("Deshacer", imagenes, fuentes, controlador::deshacer));
        acciones.add(boton("Reiniciar", imagenes, fuentes, controlador::reiniciarNivel));
        acciones.add(boton("Guardar", imagenes, fuentes, this::guardar));
        acciones.add(boton("Cargar", imagenes, fuentes, this::cargar));
        logrosObtenidos.setFont(fuentes.fuente("kenvector_future_thin", 12f));
        logrosObtenidos.setForeground(Color.WHITE);
        acciones.add(logrosObtenidos);
        add(acciones, BorderLayout.CENTER);

        logros.alDesbloquear(this::mostrarLogro);
        actualizar();
        actualizarLogros("");
    }

    private static BotonEspacial boton(String texto, RepositorioDeImagenes imagenes,
            RepositorioDeFuentes fuentes, Runnable accion) {
        return new BotonEspacial(texto, 112, 11f, imagenes, fuentes, accion);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension natural = super.getPreferredSize();
        return new Dimension(Math.max(natural.width, ANCHO_MINIMO), natural.height);
    }

    /** Guarda la partida y avisa el resultado. */
    private void guardar() {
        try {
            controlador.guardar(RUTA_DE_GUARDADO);
            JOptionPane.showMessageDialog(this, "Partida guardada en " + RUTA_DE_GUARDADO.toAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la partida: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Carga la partida guardada; devuelve si se pudo, para que las pantallas sepan si seguir. */
    boolean cargar() {
        if (!Files.exists(RUTA_DE_GUARDADO)) {
            JOptionPane.showMessageDialog(this, "No hay una partida guardada todavía.");
            return false;
        }
        try {
            controlador.cargar(RUTA_DE_GUARDADO);
            return true;
        } catch (IOException | GuardadoInvalidoException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la partida: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void mostrarLogro(Logro logro) {
        actualizarLogros(" · Nuevo: " + logro.nombre());
    }

    private void actualizarLogros(String detalle) {
        logrosObtenidos.setText("Logros: " + logros.desbloqueados().size() + "/" + logros.total() + detalle);
    }

    private void actualizar() {
        Nivel actual = partida.nivelActual();
        nivel = "Nivel " + partida.numeroDeNivel();
        movimientos = "Movimientos: " + actual.movimientos();
        empujes = "Empujes: " + actual.empujes();
        deshacer = "Deshacer usado: " + partida.usosDeDeshacer();
        repaint();
    }

    String textoDelNivel() {
        return nivel + " | " + movimientos + " | " + empujes;
    }

    String textoDeLogros() {
        return logrosObtenidos.getText();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(VELO);
        g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 16, 16);
        g2.setColor(BORDE);
        g2.drawRoundRect(4, 4, getWidth() - 9, getHeight() - 9, 16, 16);

        String[] iconos = {"icono_nivel", "icono_movimientos", "icono_empujes", "icono_deshacer", "icono_puntaje"};
        Nivel actual = partida.nivelActual();
        String[] textos = {"Nivel " + partida.numeroDeNivel(), "Movim. " + actual.movimientos(),
                "Empujes " + actual.empujes(), "Deshacer " + partida.usosDeDeshacer(), "Puntaje " + puntaje};
        g2.setFont(fuenteDeDatos);
        FontMetrics metricas = g2.getFontMetrics();
        int ancho = (getWidth() - 20) / CANTIDAD_DE_DATOS;
        int base = 12 + LADO_DE_ICONO / 2 + (metricas.getAscent() - metricas.getDescent()) / 2;
        for (int i = 0; i < CANTIDAD_DE_DATOS; i++) {
            int x = 12 + i * ancho;
            dibujarIcono(g2, imagenes.imagen(iconos[i]), x, 12);
            g2.setColor(Color.WHITE);
            g2.drawString(textos[i], x + LADO_DE_ICONO + 6, base);
        }
        g2.dispose();
    }

    /** Ajusta el ícono a un cuadrado conservando su proporción. */
    private void dibujarIcono(Graphics2D g, BufferedImage icono, int x, int y) {
        double escala = Math.min((double) LADO_DE_ICONO / icono.getWidth(), (double) LADO_DE_ICONO / icono.getHeight());
        int ancho = (int) (icono.getWidth() * escala);
        int alto = (int) (icono.getHeight() * escala);
        g.drawImage(icono, x + (LADO_DE_ICONO - ancho) / 2, y + (LADO_DE_ICONO - alto) / 2, ancho, alto, null);
    }

    @Override
    public void jugadorSeMovio() {
        actualizar();
    }

    @Override
    public void estadoRestaurado() {
        actualizar();
    }

    @Override
    public void nivelCompletado() {
        puntaje = String.valueOf(partida.resumenDelNivel().puntaje());
        repaint();
    }

    @Override
    public void nivelCambiado() {
        actualizar();
    }
}
