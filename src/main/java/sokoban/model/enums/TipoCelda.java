package sokoban.model.enums;

public enum TipoCelda {
    PARED(false),
    PISO(true),
    DESTINO(true);

    private final boolean transitable;

    TipoCelda(boolean transitable) {
        this.transitable = transitable;
    }

    public boolean esTransitable() {
        return transitable;
    }
}
