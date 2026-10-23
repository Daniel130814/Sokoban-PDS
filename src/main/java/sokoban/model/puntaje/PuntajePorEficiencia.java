package sokoban.model.puntaje;

/** Parte de un puntaje base y descuenta por movimientos, empujes y usos de deshacer; nunca baja de cero. */
public class PuntajePorEficiencia implements EstrategiaDePuntaje {

    static final int PUNTAJE_BASE = 1000;
    static final int PENALIDAD_POR_MOVIMIENTO = 10;
    static final int PENALIDAD_POR_EMPUJE = 5;
    static final int PENALIDAD_POR_DESHACER = 50;

    @Override
    public int calcular(EstadisticasDeNivel estadisticas) {
        int puntaje = PUNTAJE_BASE
                - PENALIDAD_POR_MOVIMIENTO * estadisticas.movimientos()
                - PENALIDAD_POR_EMPUJE * estadisticas.empujes()
                - PENALIDAD_POR_DESHACER * estadisticas.usosDeDeshacer();
        return Math.max(0, puntaje);
    }
}
