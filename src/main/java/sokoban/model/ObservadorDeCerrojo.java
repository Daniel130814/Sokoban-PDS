package sokoban.model;

/** Observer: reacciona cuando un cerrojo es activado por su caja llave. */
public interface ObservadorDeCerrojo {

    void alActivarseCerrojo(Cerrojo cerrojo);
}
