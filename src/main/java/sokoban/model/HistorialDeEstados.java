package sokoban.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/** Caretaker: guarda los últimos estados sin conocer su contenido. */
public class HistorialDeEstados {

    public static final int CAPACIDAD = 15;

    private final Deque<EstadoNivel> estados = new ArrayDeque<>();

    public void guardar(EstadoNivel estado) {
        estados.addLast(estado);
        if (estados.size() > CAPACIDAD) {
            estados.removeFirst();
        }
    }

    /**
     * Retrocede hasta {@code pasos} estados y devuelve el más antiguo de los retirados;
     * si hay menos guardados, retrocede lo que haya.
     */
    public Optional<EstadoNivel> retroceder(int pasos) {
        EstadoNivel ultimo = null;
        for (int i = 0; i < pasos && !estados.isEmpty(); i++) {
            ultimo = estados.removeLast();
        }
        return Optional.ofNullable(ultimo);
    }

    public int cantidad() {
        return estados.size();
    }

    public void limpiar() {
        estados.clear();
    }
}
