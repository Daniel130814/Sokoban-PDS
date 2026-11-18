package sokoban.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Cursor;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import sokoban.model.Nivel;
import sokoban.model.campania.ElementoDeCampania;
import sokoban.model.campania.Mundo;
import sokoban.model.campania.NivelDeCampania;
import sokoban.model.enums.Direccion;

/** Catálogo de niveles: una miniatura por nivel con su título y qué enseña. Solo para mirar. */
public class PantallaDeNiveles extends PanelEspacial {

    private static final Dimension TAMANIO_DE_TARJETA = new Dimension(210, 190);
    private static final int ANCHO_DE_MINIATURA = 190;
    private static final int ALTO_DE_MINIATURA = 110;

    private static final Color[] ACENTOS = {new Color(60, 200, 255), new Color(255, 200, 60), new Color(120, 230, 140)};
    private static final int SEPARACION = 12;

    private final RecursosVisuales recursos;
    private final DibujanteDeNivel dibujante;
    private final ElementoDeCampania campania;
    private final IntConsumer alElegirNivel;
    private int mundosDibujados;

    /** {@code alElegirNivel} recibe el número del nivel (desde 1) cuando el jugador hace clic en su tarjeta. */
    public PantallaDeNiveles(RecursosVisuales recursos, ElementoDeCampania campania, IntConsumer alElegirNivel,
            Runnable volver) {
        super(recursos, "fondo_menu");
        this.recursos = recursos;
        this.campania = campania;
        this.alElegirNivel = alElegirNivel;
        this.dibujante = new DibujanteDeNivel(recursos);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("NIVELES", SwingConstants.CENTER);
        titulo.setFont(recursos.fuente("kenvector_future", 32f));
        titulo.setForeground(new Color(150, 200, 255));
        titulo.setBorder(BorderFactory.createEmptyBorder(16, 0, 4, 0));
        JLabel nota = new JLabel("Elegí un nivel para jugarlo (" + campania.cantidadDeNiveles() + " en total)", SwingConstants.CENTER);
        nota.setFont(recursos.fuente("kenvector_future_thin", 13f));
        nota.setForeground(Color.WHITE);
        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(nota);
        add(encabezado, BorderLayout.NORTH);

        JPanel secciones = new JPanel();
        secciones.setLayout(new BoxLayout(secciones, BoxLayout.Y_AXIS));
        secciones.setOpaque(false);
        agregarElemento(secciones, campania);
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(secciones);
        JScrollPane desplazamiento = new JScrollPane(centro);
        desplazamiento.setOpaque(false);
        desplazamiento.getViewport().setOpaque(false);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getVerticalScrollBar().setUnitIncrement(24);
        add(desplazamiento, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        pie.setOpaque(false);
        pie.add(new BotonEspacial("Volver", 200, 14f, recursos, volver));
        add(pie, BorderLayout.SOUTH);
    }

    /** Recorre el Composite: cada mundo es una sección con una fila de tarjetas para sus niveles directos. */
    private void agregarElemento(JPanel secciones, ElementoDeCampania elemento) {
        if (!(elemento instanceof Mundo mundo)) {
            return;
        }
        List<NivelDeCampania> propios = mundo.hijos().stream()
                .filter(NivelDeCampania.class::isInstance).map(NivelDeCampania.class::cast).toList();
        if (!propios.isEmpty()) {
            Color acento = ACENTOS[mundosDibujados++ % ACENTOS.length];
            JLabel cabecera = new JLabel(mundo.titulo() + "  -  " + mundo.detalle());
            cabecera.setFont(recursos.fuente("kenvector_future", 16f));
            cabecera.setForeground(acento);
            cabecera.setBorder(BorderFactory.createEmptyBorder(14, 12, 2, 0));
            cabecera.setAlignmentX(LEFT_ALIGNMENT);
            secciones.add(cabecera);

            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, SEPARACION, SEPARACION));
            fila.setOpaque(false);
            fila.setAlignmentX(LEFT_ALIGNMENT);
            fila.setPreferredSize(new Dimension(
                    propios.size() * (TAMANIO_DE_TARJETA.width + SEPARACION) + SEPARACION,
                    TAMANIO_DE_TARJETA.height + 2 * SEPARACION));
            propios.forEach(nivel -> {
                int numero = campania.niveles().indexOf(nivel.nivel()) + 1;
                fila.add(new Tarjeta(nivel, dibujante, recursos, acento, () -> alElegirNivel.accept(numero)));
            });
            secciones.add(fila);
        }
        mundo.hijos().forEach(hijo -> agregarElemento(secciones, hijo));
    }

    private static final class Tarjeta extends JComponent {

        private final NivelDeCampania ficha;
        private final DibujanteDeNivel dibujante;
        private final Font fuenteDeTitulo;
        private final Font fuenteDeDetalle;
        private final Color acento;
        private boolean resaltada;

        Tarjeta(NivelDeCampania ficha, DibujanteDeNivel dibujante, RecursosVisuales recursos, Color acento,
                Runnable alElegir) {
            this.ficha = ficha;
            this.dibujante = dibujante;
            this.fuenteDeTitulo = recursos.fuente("kenvector_future", 14f);
            this.fuenteDeDetalle = recursos.fuente("kenvector_future_thin", 11f);
            this.acento = acento;
            setPreferredSize(TAMANIO_DE_TARJETA);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Jugar " + ficha.titulo());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    alElegir.run();
                }

                @Override
                public void mouseEntered(MouseEvent evento) {
                    resaltada = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent evento) {
                    resaltada = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            Graphics2D g = (Graphics2D) graficos.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(resaltada ? new Color(30, 40, 90, 225) : new Color(8, 10, 28, 200));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g.setColor(acento);
            g.setStroke(new java.awt.BasicStroke(resaltada ? 3f : 1f));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
            g.setStroke(new java.awt.BasicStroke(1f));

            dibujarMiniatura(g.create(10, 10, ANCHO_DE_MINIATURA, ALTO_DE_MINIATURA), ficha.nivel());

            g.setFont(fuenteDeTitulo);
            g.setColor(acento);
            g.drawString(ficha.titulo(), 12, 148);
            g.setFont(fuenteDeDetalle);
            g.setColor(Color.WHITE);
            g.drawString(ficha.detalle(), 12, 170);
            g.dispose();
        }

        /** Escala el nivel para que entre completo en el recuadro y lo centra. */
        private void dibujarMiniatura(Graphics g, Nivel nivel) {
            Graphics2D g2 = (Graphics2D) g;
            double ancho = nivel.tablero().ancho() * PanelDeTablero.TAMANIO_CELDA;
            double alto = nivel.tablero().alto() * PanelDeTablero.TAMANIO_CELDA;
            double escala = Math.min(ANCHO_DE_MINIATURA / ancho, ALTO_DE_MINIATURA / alto);
            g2.translate((ANCHO_DE_MINIATURA - ancho * escala) / 2, (ALTO_DE_MINIATURA - alto * escala) / 2);
            g2.scale(escala, escala);
            dibujante.dibujar(g2, nivel, Direccion.ARRIBA);
            g2.dispose();
        }
    }
}
