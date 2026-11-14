package sokoban.view;

import sokoban.model.puntaje.ResumenDeNivel;

/** Resumen de un nivel terminado, con un botón para seguir al próximo. */
public class PantallaNivelCompletado extends PantallaCentrada {

    public PantallaNivelCompletado(RecursosVisuales recursos,
            Runnable continuar) {
        super("fondo_espacio", "NIVEL COMPLETADO", 30f, recursos);
        agregarBoton("Continuar", continuar);
    }

    public void mostrar(ResumenDeNivel resumen, String tiempo) {
        mostrarTexto("Movimientos: " + resumen.estadisticas().movimientos()
                + "\nEmpujes: " + resumen.estadisticas().empujes()
                + "\nDeshacer usado: " + resumen.estadisticas().usosDeDeshacer() + " veces"
                + "\nTiempo: " + tiempo
                + "\n\nPuntaje final: " + resumen.puntaje());
    }
}
