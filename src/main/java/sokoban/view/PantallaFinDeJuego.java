package sokoban.view;

/** Pantalla final: se completaron todos los niveles. */
public class PantallaFinDeJuego extends PantallaCentrada {

    public PantallaFinDeJuego(RepositorioDeImagenes imagenes, RepositorioDeFuentes fuentes, Runnable cargar,
            Runnable salir) {
        super("fondo_menu", "FIN DEL JUEGO", 34f, imagenes, fuentes);
        mostrarTexto("¡Completaste todos los niveles!\nLa estación quedó en orden.");
        agregarBoton("Cargar partida", cargar);
        agregarBoton("Salir", salir);
    }
}
