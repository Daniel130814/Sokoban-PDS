package sokoban.view;

/** Pantalla inicial: jugar, ver los niveles, cargar una partida guardada o salir. */
public class PantallaDeMenu extends PantallaCentrada {

    public PantallaDeMenu(RecursosVisuales recursos, Runnable jugar,
            Runnable verNiveles, Runnable cargar, Runnable salir) {
        super("fondo_menu", "SOKOBAN", 44f, recursos);
        mostrarTexto("Estación espacial\nEmpujá los módulos de carga hasta sus bahías");
        agregarBoton("Jugar", jugar);
        agregarBoton("Niveles", verNiveles);
        agregarBoton("Cargar partida", cargar);
        agregarBoton("Salir", salir);
    }
}
