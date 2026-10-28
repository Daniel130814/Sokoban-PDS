package sokoban.view;

import sokoban.model.puntaje.ResumenDeNivel;

/** Resumen de un nivel terminado, con un botón para seguir al próximo. */
public class PantallaNivelCompletado extends PantallaCentrada {

    public PantallaNivelCompletado(RepositorioDeImagenes imagenes, RepositorioDeFuentes fuentes,
            Runnable continuar) {
        super("fondo_espacio", "NIVEL COMPLETADO", 30f, imagenes, fuentes);
        agregarBoton("Continuar", continuar);
    }

    public void mostrar(ResumenDeNivel resumen) {
        mostrarTexto("Movimientos: " + resumen.estadisticas().movimientos()
                + "\nEmpujes: " + resumen.estadisticas().empujes()
                + "\nDeshacer usado: " + resumen.estadisticas().usosDeDeshacer() + " veces"
                + "\n\nPuntaje final: " + resumen.puntaje());
    }
}
