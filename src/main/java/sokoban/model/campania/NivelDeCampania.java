package sokoban.model.campania;

import java.util.List;
import sokoban.model.Nivel;

/** Leaf del Composite: un único nivel con su título y qué enseña. */
public class NivelDeCampania implements ElementoDeCampania {

    private final String titulo;
    private final String detalle;
    private final Nivel nivel;

    public NivelDeCampania(String titulo, String detalle, Nivel nivel) {
        this.titulo = titulo;
        this.detalle = detalle;
        this.nivel = nivel;
    }

    @Override
    public String titulo() {
        return titulo;
    }

    @Override
    public String detalle() {
        return detalle;
    }

    public Nivel nivel() {
        return nivel;
    }

    @Override
    public List<Nivel> niveles() {
        return List.of(nivel);
    }
}
