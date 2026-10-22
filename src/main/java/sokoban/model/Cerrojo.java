package sokoban.model;

import java.util.ArrayList;
import java.util.List;

/** Subject del Observer: avisa a sus observadores la primera vez que se activa. */
public class Cerrojo {

    private final int id;
    private final Posicion posicion;
    private final List<ObservadorDeCerrojo> observadores = new ArrayList<>();
    private boolean activo;

    public Cerrojo(int id, Posicion posicion) {
        this.id = id;
        this.posicion = posicion;
    }

    public int id() {
        return id;
    }

    public Posicion posicion() {
        return posicion;
    }

    public boolean estaActivo() {
        return activo;
    }

    public void agregarObservador(ObservadorDeCerrojo observador) {
        observadores.add(observador);
    }

    public void activar() {
        if (activo) {
            return;
        }
        activo = true;
        observadores.forEach(observador -> observador.alActivarseCerrojo(this));
    }
}
