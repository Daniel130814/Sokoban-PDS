package sokoban.view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Flyweight Factory: cada imagen se carga una sola vez y se comparte entre todas las celdas que
 * la usan, en lugar de leer el mismo PNG cientos de veces en cada repintado.
 */
public class RepositorioDeImagenes {

    private final Map<String, BufferedImage> imagenes = new HashMap<>();

    public BufferedImage imagen(String nombre) {
        return imagenes.computeIfAbsent(nombre, this::cargar);
    }

    public int cantidadCargadas() {
        return imagenes.size();
    }

    private BufferedImage cargar(String nombre) {
        String ruta = "/images/" + nombre + ".png";
        try (InputStream entrada = RepositorioDeImagenes.class.getResourceAsStream(ruta)) {
            if (entrada == null) {
                throw new IllegalStateException("Falta la imagen " + ruta);
            }
            return ImageIO.read(entrada);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la imagen " + ruta, e);
        }
    }
}
