package sokoban.view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga cada fuente TrueType una sola vez (misma idea de cache que {@link RepositorioDeImagenes}).
 * Si una fuente no se puede leer, devuelve una fuente lógica de Swing para no frenar el juego.
 */
public class RepositorioDeFuentes {

    private final Map<String, Font> fuentes = new HashMap<>();

    public Font fuente(String nombre, float tamanio) {
        return fuentes.computeIfAbsent(nombre, this::cargar).deriveFont(tamanio);
    }

    public int cantidadCargadas() {
        return fuentes.size();
    }

    private Font cargar(String nombre) {
        String ruta = "/fonts/" + nombre + ".ttf";
        try (InputStream entrada = RepositorioDeFuentes.class.getResourceAsStream(ruta)) {
            if (entrada == null) {
                throw new IOException("Falta la fuente " + ruta);
            }
            return Font.createFont(Font.TRUETYPE_FONT, entrada);
        } catch (IOException | FontFormatException e) {
            System.err.println("No se pudo cargar la fuente " + ruta + ": " + e.getMessage());
            return new Font(Font.SANS_SERIF, Font.BOLD, 12);
        }
    }
}
