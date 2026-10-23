package sokoban.model;

/** Memento de una caja: captura su estado interno y permite devolverla a ese estado. */
interface EstadoCaja {

    void restaurar();
}
