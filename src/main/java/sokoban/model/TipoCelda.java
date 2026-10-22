package sokoban.model;

public enum TipoCelda {
    PARED(false, new MovimientoNormal()),
    PISO(true, new MovimientoNormal()),
    DESTINO(true, new MovimientoNormal()),
    HIELO(true, new Deslizamiento()),
    CERROJO(true, new MovimientoNormal()),
    MURO_CERRADO(false, new MovimientoNormal()),
    MURO_ABIERTO(true, new MovimientoNormal());

    private final boolean transitable;
    private final ReglaDeMovimiento reglaDeMovimiento;

    TipoCelda(boolean transitable, ReglaDeMovimiento reglaDeMovimiento) {
        this.transitable = transitable;
        this.reglaDeMovimiento = reglaDeMovimiento;
    }

    public boolean esTransitable() {
        return transitable;
    }

    public ReglaDeMovimiento reglaDeMovimiento() {
        return reglaDeMovimiento;
    }
}
