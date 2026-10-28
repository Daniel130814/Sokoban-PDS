package sokoban.view;

/** Pantalla inicial: jugar, cargar una partida guardada o salir. */
public class PantallaDeMenu extends PantallaCentrada {

    public PantallaDeMenu(RepositorioDeImagenes imagenes, RepositorioDeFuentes fuentes, Runnable jugar,
            Runnable cargar, Runnable salir) {
        super("fondo_menu", "SOKOBAN", 44f, imagenes, fuentes);
        mostrarTexto("Estación espacial\nEmpujá los módulos de carga hasta sus bahías");
        agregarBoton("Jugar", jugar);
        agregarBoton("Cargar partida", cargar);
        agregarBoton("Salir", salir);
    }
}
