package sokoban.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/** Panel con un fondo de estrellas hecho repitiendo una imagen del repositorio. */
public class PanelEspacial extends JPanel {

    private final TexturePaint textura;

    public PanelEspacial(RepositorioDeImagenes imagenes, String fondo) {
        BufferedImage tile = imagenes.imagen(fondo);
        this.textura = new TexturePaint(tile, new Rectangle(0, 0, tile.getWidth(), tile.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(textura);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
