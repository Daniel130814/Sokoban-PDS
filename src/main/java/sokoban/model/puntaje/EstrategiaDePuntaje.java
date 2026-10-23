package sokoban.model.puntaje;

/** Strategy: el criterio de puntaje es libre y probablemente cambie, así que vive detrás de esta interfaz. */
public interface EstrategiaDePuntaje {

    int calcular(EstadisticasDeNivel estadisticas);
}
