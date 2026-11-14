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
import sokoban.exception.GuardadoInvalidoException;
import sokoban.model.enums.Direccion;
import sokoban.model.modo.ModoDeJuego;
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

    private final List<ModoDeJuego> modos;

    public ControladorDeJuego(Partida partida) {
        this(partida, List.of(partida.modo()));
    }

    /** Recibe los modos entre los que se puede elegir al empezar y al cargar una partida. */
    public ControladorDeJuego(Partida partida, List<ModoDeJuego> modos) {
        this.partida = partida;
        this.modos = List.copyOf(modos);
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

    /** Vuelve al primer nivel con todo como al empezar y borra el registro de acciones. */
    public void nuevaPartida() {
        partida.reconstruir(nueva -> { });
        registro.clear();
    }

    public void guardar(Path ruta) throws IOException {
        guardado.guardar(partida.modo().nombre(), registro, ruta);
    }

    /** Empieza una partida nueva con las reglas del modo elegido. */
    public void elegirModo(ModoDeJuego modo) {
        partida.establecerModo(modo);
        nuevaPartida();
    }

    private ModoDeJuego buscarModo(String nombre) {
        return modos.stream().filter(modo -> modo.nombre().equals(nombre)).findFirst()
                .orElseThrow(() -> new GuardadoInvalidoException("Modo de juego desconocido: " + nombre));
    }

    /** Vuelve al principio de la partida y reproduce las acciones guardadas, sin avisar de cada una. */
    public void cargar(Path ruta) throws IOException {
        List<Accion> acciones = guardado.leer(ruta);
        // Un guardado sin modo (anterior a los modos) se juega con el modo que está activo.
        guardado.leerModo(ruta).ifPresent(nombre -> partida.establecerModo(buscarModo(nombre)));
        partida.reconstruir(nueva -> acciones.forEach(accion -> accion.ejecutar(nueva)));
        registro.clear();
        registro.addAll(acciones);
    }
}
