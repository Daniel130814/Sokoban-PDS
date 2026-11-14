package sokoban.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sokoban.logros.EstadisticasDeJuego;
import sokoban.logros.Logro;
import sokoban.logros.Logros;

/**
 * Lista de logros: cómo conseguir cada uno y una barra de progreso hacia él, más una barra general de
 * experiencia (logros desbloqueados sobre el total). Lee el estado de {@link Logros} al dibujar.
 */
public class PantallaDeLogros extends PanelEspacial {

    private static final Color VELO = new Color(8, 10, 28, 190);
    private static final Color FONDO_DE_BARRA = new Color(255, 255, 255, 40);
    private static final Color BARRA_EN_CURSO = new Color(60, 200, 255);
    private static final Color BARRA_COMPLETA = new Color(255, 200, 60);
    private static final int ALTO_DE_FILA = 74;
    private static final int ANCHO = 600;

    private final Logros logros;
    private final Font fuenteDeNombre;
    private final Font fuenteDeTexto;
    private final JComponent lista = new JComponent() {
        @Override
        protected void paintComponent(Graphics g) {
            dibujarLista((Graphics2D) g.create());
        }
    };

    public PantallaDeLogros(RecursosVisuales recursos, Logros logros,
            Runnable volver) {
        super(recursos, "fondo_menu");
        this.logros = logros;
        this.fuenteDeNombre = recursos.fuente("kenvector_future", 14f);
        this.fuenteDeTexto = recursos.fuente("kenvector_future_thin", 12f);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("LOGROS", SwingConstants.CENTER);
        titulo.setFont(recursos.fuente("kenvector_future", 32f));
        titulo.setForeground(new Color(150, 200, 255));
        titulo.setBorder(BorderFactory.createEmptyBorder(16, 0, 8, 0));
        add(titulo, BorderLayout.NORTH);

        lista.setPreferredSize(new Dimension(ANCHO, (logros.total() + 1) * ALTO_DE_FILA + 10));
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(lista);
        add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        pie.setOpaque(false);
        pie.add(new BotonEspacial("Volver", 200, 14f, recursos, volver));
        add(pie, BorderLayout.SOUTH);
    }

    private void dibujarLista(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        EstadisticasDeJuego estadisticas = logros.estadisticas();
        int desbloqueados = logros.desbloqueados().size();
        int y = 0;
        dibujarFila(g, y, "Experiencia", "Cada logro desbloqueado suma al total", desbloqueados, logros.total(),
                desbloqueados == logros.total());
        for (Logro logro : logros.catalogo()) {
            y += ALTO_DE_FILA;
            dibujarFila(g, y, logro.nombre(), logro.descripcion(), logro.avance(estadisticas), logro.meta(),
                    logros.desbloqueados().contains(logro));
        }
        g.dispose();
    }

    private void dibujarFila(Graphics2D g, int y, String nombre, String como, int avance, int meta,
            boolean completo) {
        g.setColor(VELO);
        g.fillRoundRect(0, y + 2, ANCHO, ALTO_DE_FILA - 6, 14, 14);
        Color acento = completo ? BARRA_COMPLETA : BARRA_EN_CURSO;

        g.setFont(fuenteDeNombre);
        g.setColor(completo ? BARRA_COMPLETA : Color.WHITE);
        g.drawString(nombre, 14, y + 24);
        String cuenta = completo ? "COMPLETADO" : avance + " / " + meta;
        FontMetrics metricas = g.getFontMetrics(fuenteDeTexto);
        g.setFont(fuenteDeTexto);
        g.setColor(acento);
        g.drawString(cuenta, ANCHO - 14 - metricas.stringWidth(cuenta), y + 24);

        g.setColor(new Color(200, 210, 230));
        g.drawString("Cómo: " + como, 14, y + 44);

        int anchoDeBarra = ANCHO - 28;
        g.setColor(FONDO_DE_BARRA);
        g.fillRoundRect(14, y + 52, anchoDeBarra, 10, 10, 10);
        g.setColor(acento);
        g.fillRoundRect(14, y + 52, anchoDeBarra * avance / Math.max(1, meta), 10, 10, 10);
    }
}
