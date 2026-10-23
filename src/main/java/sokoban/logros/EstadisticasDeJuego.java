package sokoban.logros;

/** Contadores acumulados de lo que el jugador hizo durante la sesión. */
public class EstadisticasDeJuego {

    private int empujes;
    private int cajasRotas;
    private int cerrojosActivados;
    private int nivelesCompletados;
    private int nivelesSinDeshacer;

    void registrarEmpuje() {
        empujes++;
    }

    void registrarCajaRota() {
        cajasRotas++;
    }

    void registrarCerrojoActivado() {
        cerrojosActivados++;
    }

    void registrarNivelCompletado(boolean sinDeshacer) {
        nivelesCompletados++;
        if (sinDeshacer) {
            nivelesSinDeshacer++;
        }
    }

    public int empujes() {
        return empujes;
    }

    public int cajasRotas() {
        return cajasRotas;
    }

    public int cerrojosActivados() {
        return cerrojosActivados;
    }

    public int nivelesCompletados() {
        return nivelesCompletados;
    }

    public int nivelesSinDeshacer() {
        return nivelesSinDeshacer;
    }
}
