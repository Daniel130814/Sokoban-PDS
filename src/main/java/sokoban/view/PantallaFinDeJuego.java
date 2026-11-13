package sokoban.view;

/** Pantalla final: se completaron todos los niveles; se puede volver al inicio para jugar de nuevo. */
public class PantallaFinDeJuego extends PantallaCentrada {

    public PantallaFinDeJuego(RepositorioDeImagenes imagenes, RepositorioDeFuentes fuentes, Runnable alInicio,
            Runnable salir) {
        super("fondo_menu", "FIN DEL JUEGO", 34f, imagenes, fuentes);
        mostrarTexto("¡Completaste todos los niveles!\nLa estación quedó en orden.");
        agregarBoton("Volver al inicio", alInicio);
        agregarBoton("Salir", salir);
    }
}
