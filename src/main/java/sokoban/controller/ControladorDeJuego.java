package sokoban.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import sokoban.controller.accion.Accion;
import sokoban.controller.accion.Continuar;
import sokoban.controller.accion.Deshacer;
import sokoban.controller.accion.Mover;
import sokoban.controller.accion.Reiniciar;
import sokoban.model.Partida;
import sokoban.model.enums.Direccion;
import sokoban.persistencia.GuardadoDePartida;

/**
 * GRASP Controller e Invoker del Command: traduce las acciones del usuario (teclas, botones) en
 * objetos {@link Accion}, las ejecuta sobre el modelo y registra las que tuvieron efecto.
 * No depende de Swing, por lo que se puede probar sin interfaz.
 */
public class ControladorDeJuego {

    private final Partida partida;
    private final GuardadoDePartida guardado = new GuardadoDePartida();
    private final List<Accion> registro = new ArrayList<>();

    public ControladorDeJuego(Partida partida) {
        this.partida = partida;
    }

    public void ejecutar(Accion accion) {
        if (accion.ejecutar(partida)) {
            registro.add(accion);
        }
    }

    public void mover(Direccion direccion) {
        ejecutar(new Mover(direccion));
    }

    public void deshacer() {
        ejecutar(new Deshacer());
    }

    public void reiniciarNivel() {
        ejecutar(new Reiniciar());
    }

    public void continuar() {
        ejecutar(new Continuar());
    }

    public void guardar(Path ruta) throws IOException {
        guardado.guardar(registro, ruta);
    }

    /** Vuelve al principio de la partida y reproduce las acciones guardadas, sin avisar de cada una. */
    public void cargar(Path ruta) throws IOException {
        List<Accion> acciones = guardado.leer(ruta);
        partida.reconstruir(nueva -> acciones.forEach(accion -> accion.ejecutar(nueva)));
        registro.clear();
        registro.addAll(acciones);
    }
}
