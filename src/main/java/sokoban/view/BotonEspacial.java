package sokoban.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;

/** Botón con la imagen de botón del pack y la fuente del tema; no toma el foco del teclado. */
public class BotonEspacial extends JButton {

    private final RepositorioDeImagenes imagenes;

    public BotonEspacial(String texto, int ancho, float tamanioDeFuente, RepositorioDeImagenes imagenes,
            RepositorioDeFuentes fuentes, Runnable accion) {
        super(texto);
        this.imagenes = imagenes;
        setFont(fuentes.fuente("kenvector_future_thin", tamanioDeFuente));
        setForeground(new Color(20, 40, 90));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        setPreferredSize(new Dimension(ancho, (int) (ancho * 39.0 / 222.0) + 8));
        addActionListener(evento -> accion.run());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.drawImage(imagenes.imagen("boton"), 0, 0, getWidth(), getHeight(), null);
        if (getModel().isPressed()) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else if (getModel().isRollover()) {
            g2.setColor(new Color(255, 255, 255, 45));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
