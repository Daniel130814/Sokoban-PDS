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
import java.awt.event.HierarchyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import sokoban.controller.ControladorDeJuego;
import sokoban.exception.GuardadoInvalidoException;
import sokoban.logros.Logro;
import sokoban.logros.Logros;
import sokoban.model.Nivel;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;
import sokoban.tiempo.Cronometro;

/**
 * HUD dibujado a mano sobre un panel semitransparente: nivel, movimientos, empujes, deshacer y último
 * puntaje y reloj con su ícono, más los botones de deshacer, reiniciar, guardar, cargar, logros e inicio.
 */
public class PanelHud extends JPanel implements ObservadorDeJuego {

    static final Path RUTA_DE_GUARDADO = Path.of("partida-guardada.txt");

    private static final Color VELO = new Color(8, 10, 28, 175);
    private static final Color BORDE = new Color(120, 170, 255, 120);
    private static final int ALTO_DE_DATOS = 40;
    private static final int ANCHO_MINIMO = 780;
    private static final int MILISEGUNDOS_ENTRE_REVISIONES = 250;
    private static final int LADO_DE_ICONO = 24;
    private static final int CANTIDAD_DE_DATOS = 6;

    private final Partida partida;
    private final Logros logros;
    private final ControladorDeJuego controlador;
    private final RecursosVisuales recursos;
    private final Cronometro cronometro;
    private final Font fuenteDeDatos;
    private final JLabel logrosObtenidos = new JLabel();
    private String nivel = "";
    private String movimientos = "";
    private String empujes = "";
    private String deshacer = "";
    private String puntaje = "-";

    public PanelHud(Partida partida, ControladorDeJuego controlador, Logros logros,
            RecursosVisuales recursos, Cronometro cronometro, Runnable verLogros, Runnable alInicio) {
        super(new BorderLayout());
        this.partida = partida;
        this.controlador = controlador;
        this.logros = logros;
        this.recursos = recursos;
        this.cronometro = cronometro;
        this.fuenteDeDatos = recursos.fuente("kenvector_future_thin", 12f);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(ALTO_DE_DATOS, 10, 8, 10));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(boton("Deshacer", recursos, controlador::deshacer));
        acciones.add(boton("Reiniciar", recursos, controlador::reiniciarNivel));
        acciones.add(boton("Guardar", recursos, this::guardar));
        acciones.add(boton("Cargar", recursos, this::cargar));
        acciones.add(boton("Logros", recursos, verLogros));
        acciones.add(boton("Inicio", recursos, alInicio));
        logrosObtenidos.setFont(recursos.fuente("kenvector_future_thin", 12f));
        logrosObtenidos.setForeground(Color.WHITE);
        acciones.add(logrosObtenidos);
        add(acciones, BorderLayout.CENTER);

        logros.alDesbloquear(this::mostrarLogro);
        iniciarRelojMientrasSeVe();
        actualizar();
        actualizarLogros("");
    }

    /**
     * Mientras el HUD está en pantalla, cada pocos milisegundos repinta el reloj y le pide a la partida que
     * revise el límite del modo (así Contrarreloj pierde aunque el jugador no se mueva).
     */
    private void iniciarRelojMientrasSeVe() {
        Timer reloj = new Timer(MILISEGUNDOS_ENTRE_REVISIONES, evento -> {
            partida.revisarLimite();
            repaint();
        });
        addHierarchyListener(evento -> {
            if ((evento.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    reloj.start();
                } else {
                    reloj.stop();
                }
            }
        });
    }

    String textoDeTiempo() {
        return partida.modo().etiquetaDeTiempo(cronometro.transcurrido());
    }

    private static BotonEspacial boton(String texto, RecursosVisuales recursos, Runnable accion) {
        return new BotonEspacial(texto, 112, 11f, recursos, accion);
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

        String[] iconos = {"icono_nivel", "icono_movimientos", "icono_empujes", "icono_deshacer", "icono_puntaje",
                null};
        Nivel actual = partida.nivelActual();
        String[] textos = {"Nivel " + partida.numeroDeNivel(), "Movim. " + partida.modo().etiquetaDeMovimientos(actual),
                "Empujes " + actual.empujes(), "Deshacer " + partida.usosDeDeshacer(), "Puntaje " + puntaje,
                textoDeTiempo()};
        g2.setFont(fuenteDeDatos);
        FontMetrics metricas = g2.getFontMetrics();
        int ancho = (getWidth() - 20) / CANTIDAD_DE_DATOS;
        int base = 12 + LADO_DE_ICONO / 2 + (metricas.getAscent() - metricas.getDescent()) / 2;
        for (int i = 0; i < CANTIDAD_DE_DATOS; i++) {
            int x = 12 + i * ancho;
            if (iconos[i] == null) {
                dibujarReloj(g2, x, 12);
            } else {
                dibujarIcono(g2, recursos.imagen(iconos[i]), x, 12);
            }
            g2.setColor(Color.WHITE);
            g2.drawString(textos[i], x + LADO_DE_ICONO + 6, base);
        }
        g2.dispose();
    }

    /** Ícono de reloj dibujado con formas (no hay un reloj en el pack de assets). */
    private void dibujarReloj(Graphics2D g, int x, int y) {
        double centroX = x + LADO_DE_ICONO / 2.0;
        double centroY = y + LADO_DE_ICONO / 2.0;
        double radio = LADO_DE_ICONO / 2.0 - 2;
        g.setColor(new Color(150, 200, 255));
        g.setStroke(new java.awt.BasicStroke(2f));
        g.draw(new Ellipse2D.Double(centroX - radio, centroY - radio, radio * 2, radio * 2));
        g.draw(new Line2D.Double(centroX, centroY, centroX, centroY - radio * 0.65));
        g.draw(new Line2D.Double(centroX, centroY, centroX + radio * 0.5, centroY));
        g.setStroke(new java.awt.BasicStroke(1f));
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
