package sokoban.view;

import java.util.List;
import java.util.function.Consumer;
import sokoban.model.modo.ModoDeJuego;

/** Elegir con qué reglas se juega: un botón por modo, con su descripción arriba. */
public class PantallaDeModos extends PantallaCentrada {

    public PantallaDeModos(RecursosVisuales recursos, List<ModoDeJuego> modos, Consumer<ModoDeJuego> elegir,
            Runnable volver) {
        super("fondo_menu", "MODO DE JUEGO", 34f, recursos);
        mostrarTexto(String.join("\n", modos.stream().map(modo -> modo.nombre() + ": " + modo.descripcion()).toList()));
        modos.forEach(modo -> agregarBoton(modo.nombre(), () -> elegir.accept(modo)));
        agregarBoton("Volver", volver);
    }
}
