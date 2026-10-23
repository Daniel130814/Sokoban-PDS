package sokoban.model;

import java.util.List;
import java.util.Map;

/**
 * Memento: instantánea inmutable del estado de un {@link Nivel}. Es opaco para quien la guarda
 * (el historial): solo el {@code Nivel} puede leer su contenido.
 */
public final class EstadoNivel {

    private final Posicion jugador;
    private final List<Caja> cajas;
    private final List<EstadoCaja> estadosDeCajas;
    private final TipoCelda[][] celdas;
    private final Map<Cerrojo, Boolean> cerrojosActivos;
    private final int movimientos;
    private final int empujes;

    EstadoNivel(Posicion jugador, List<Caja> cajas, List<EstadoCaja> estadosDeCajas, TipoCelda[][] celdas,
            Map<Cerrojo, Boolean> cerrojosActivos, int movimientos, int empujes) {
        this.jugador = jugador;
        this.cajas = List.copyOf(cajas);
        this.estadosDeCajas = List.copyOf(estadosDeCajas);
        this.celdas = celdas;
        this.cerrojosActivos = Map.copyOf(cerrojosActivos);
        this.movimientos = movimientos;
        this.empujes = empujes;
    }

    Posicion jugador() {
        return jugador;
    }

    List<Caja> cajas() {
        return cajas;
    }

    List<EstadoCaja> estadosDeCajas() {
        return estadosDeCajas;
    }

    TipoCelda[][] celdas() {
        return celdas;
    }

    Map<Cerrojo, Boolean> cerrojosActivos() {
        return cerrojosActivos;
    }

    int movimientos() {
        return movimientos;
    }

    int empujes() {
        return empujes;
    }
}
