package sokoban.model;

/** Observer concreto: un muro que se abre cuando se activa el cerrojo al que está asociado. */
public class Muro implements ObservadorDeCerrojo {

    private final Tablero tablero;
    private final Posicion posicion;

    public Muro(Tablero tablero, Posicion posicion) {
        this.tablero = tablero;
        this.posicion = posicion;
    }

    @Override
    public void alActivarseCerrojo(Cerrojo cerrojo) {
        tablero.abrirMuro(posicion);
    }
}
