package sokoban.model.campania;

import java.util.List;
import sokoban.model.Nivel;

/**
 * Component del Composite: tanto un nivel suelto como un mundo (o la campaña entera) se tratan igual
 * para saber cómo se llaman, cuántos niveles tienen y cuáles son, en orden de juego.
 */
public interface ElementoDeCampania {

    String titulo();

    String detalle();

    /** Todos los niveles de este elemento, en orden y sin anidar. */
    List<Nivel> niveles();

    default int cantidadDeNiveles() {
        return niveles().size();
    }
}
