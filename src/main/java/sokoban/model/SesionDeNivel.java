package sokoban.model;

import java.util.Optional;
import sokoban.model.enums.Direccion;

/** Un intento de resolver un nivel: encapsula movimientos, historial, undo y reinicio. */
public class SesionDeNivel {

    public static final int PASOS_POR_DESHACER = 5;
    public static final int MAX_USOS_CONSECUTIVOS = 3;

    private final Nivel nivel;
    private final HistorialDeEstados historial = new HistorialDeEstados();
    private final EstadoNivel estadoInicial;
    private int usosConsecutivos;
    private int usosDeDeshacer;

    public SesionDeNivel(Nivel nivel) {
        this.nivel = nivel;
        this.estadoInicial = nivel.guardarEstado();
    }

    public Nivel nivel() {
        return nivel;
    }

    public int usosDeDeshacer() {
        return usosDeDeshacer;
    }

    public boolean mover(Direccion direccion) {
        EstadoNivel antes = nivel.guardarEstado();
        boolean seMovio = nivel.moverJugador(direccion);
        if (seMovio) {
            historial.guardar(antes);
            usosConsecutivos = 0;
        }
        return seMovio;
    }

    public boolean deshacer() {
        if (usosConsecutivos >= MAX_USOS_CONSECUTIVOS) {
            return false;
        }
        Optional<EstadoNivel> anterior = historial.retroceder(PASOS_POR_DESHACER);
        if (anterior.isEmpty()) {
            return false;
        }
        nivel.restaurar(anterior.get());
        usosConsecutivos++;
        usosDeDeshacer++;
        return true;
    }

    public void reiniciar() {
        nivel.restaurar(estadoInicial);
        historial.limpiar();
        usosConsecutivos = 0;
        usosDeDeshacer = 0;
    }
}
