package sokoban.model.campania;

import java.util.ArrayList;
import java.util.List;
import sokoban.model.Nivel;

/**
 * Composite: agrupa niveles u otros mundos. La campaña completa es un mundo cuyos hijos son mundos.
 * Sus niveles son los de sus hijos, en el orden en que fueron agregados.
 */
public class Mundo implements ElementoDeCampania {

    private final String titulo;
    private final String detalle;
    private final List<ElementoDeCampania> hijos = new ArrayList<>();

    public Mundo(String titulo, String detalle) {
        this.titulo = titulo;
        this.detalle = detalle;
    }

    public Mundo agregar(ElementoDeCampania hijo) {
        hijos.add(hijo);
        return this;
    }

    public List<ElementoDeCampania> hijos() {
        return List.copyOf(hijos);
    }

    @Override
    public String titulo() {
        return titulo;
    }

    @Override
    public String detalle() {
        return detalle;
    }

    @Override
    public List<Nivel> niveles() {
        List<Nivel> todos = new ArrayList<>();
        hijos.forEach(hijo -> todos.addAll(hijo.niveles()));
        return List.copyOf(todos);
    }
}
