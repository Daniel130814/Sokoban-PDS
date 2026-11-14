package sokoban.view;

/** Se agotó el límite del modo: se puede reintentar el nivel o volver al menú. */
public class PantallaNivelFallido extends PantallaCentrada {

    public PantallaNivelFallido(RecursosVisuales recursos, Runnable reintentar, Runnable alMenu) {
        super("fondo_espacio", "NIVEL FALLIDO", 30f, recursos);
        agregarBoton("Reintentar", reintentar);
        agregarBoton("Menú principal", alMenu);
    }

    public void mostrar(String modo, String motivo) {
        mostrarTexto("Modo " + modo + "\n" + motivo);
    }
}
