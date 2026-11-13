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
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sokoban.model.Nivel;
import sokoban.model.enums.Direccion;

/** Catálogo de niveles: una miniatura por nivel con su título y qué enseña. Solo para mirar. */
public class PantallaDeNiveles extends PanelEspacial {

    private static final Dimension TAMANIO_DE_TARJETA = new Dimension(210, 190);
    private static final int ANCHO_DE_MINIATURA = 190;
    private static final int ALTO_DE_MINIATURA = 110;

    public PantallaDeNiveles(RepositorioDeImagenes imagenes, RepositorioDeFuentes fuentes,
            List<FichaDeNivel> fichas, Runnable volver) {
        super(imagenes, "fondo_menu");
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("NIVELES", SwingConstants.CENTER);
        titulo.setFont(fuentes.fuente("kenvector_future", 32f));
        titulo.setForeground(new Color(150, 200, 255));
        titulo.setBorder(BorderFactory.createEmptyBorder(16, 0, 4, 0));
        JLabel nota = new JLabel("Los primeros 4 niveles son el tutorial", SwingConstants.CENTER);
        nota.setFont(fuentes.fuente("kenvector_future_thin", 13f));
        nota.setForeground(Color.WHITE);
        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(nota);
        add(encabezado, BorderLayout.NORTH);

        DibujanteDeNivel dibujante = new DibujanteDeNivel(imagenes);
        JPanel cuadricula = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        cuadricula.setOpaque(false);
        cuadricula.setPreferredSize(new Dimension(3 * (TAMANIO_DE_TARJETA.width + 12) + 12,
                2 * (TAMANIO_DE_TARJETA.height + 12) + 12));
        for (int i = 0; i < fichas.size(); i++) {
            cuadricula.add(new Tarjeta(fichas.get(i), dibujante, fuentes, i < 4));
        }
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(cuadricula);
        add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        pie.setOpaque(false);
        pie.add(new BotonEspacial("Volver", 200, 14f, imagenes, fuentes, volver));
        add(pie, BorderLayout.SOUTH);
    }

    private static final class Tarjeta extends JComponent {

        private final FichaDeNivel ficha;
        private final DibujanteDeNivel dibujante;
        private final Font fuenteDeTitulo;
        private final Font fuenteDeDetalle;
        private final Color acento;

        Tarjeta(FichaDeNivel ficha, DibujanteDeNivel dibujante, RepositorioDeFuentes fuentes, boolean tutorial) {
            this.ficha = ficha;
            this.dibujante = dibujante;
            this.fuenteDeTitulo = fuentes.fuente("kenvector_future", 14f);
            this.fuenteDeDetalle = fuentes.fuente("kenvector_future_thin", 11f);
            this.acento = tutorial ? new Color(60, 200, 255) : new Color(255, 200, 60);
            setPreferredSize(TAMANIO_DE_TARJETA);
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            Graphics2D g = (Graphics2D) graficos.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(new Color(8, 10, 28, 200));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g.setColor(acento);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

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
