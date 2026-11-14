package sokoban.view;

import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * Facade: punto de entrada único a los recursos gráficos (imágenes y fuentes). Las pantallas piden
 * lo que necesitan acá y no conocen cómo ni de dónde se cargan cada tipo de recurso.
 */
public class RecursosVisuales {

    private final RepositorioDeImagenes imagenes;
    private final RepositorioDeFuentes fuentes;

    public RecursosVisuales() {
        this(new RepositorioDeImagenes(), new RepositorioDeFuentes());
    }

    public RecursosVisuales(RepositorioDeImagenes imagenes, RepositorioDeFuentes fuentes) {
        this.imagenes = imagenes;
        this.fuentes = fuentes;
    }

    public BufferedImage imagen(String nombre) {
        return imagenes.imagen(nombre);
    }

    public Font fuente(String nombre, float tamanio) {
        return fuentes.fuente(nombre, tamanio);
    }
}
