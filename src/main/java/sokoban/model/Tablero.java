package sokoban.model;

public class Tablero {

    private final TipoCelda[][] celdas;

    public Tablero(TipoCelda[][] celdas) {
        this.celdas = copiar(celdas);
    }

    public int alto() {
        return celdas.length;
    }

    public int ancho() {
        return celdas[0].length;
    }

    public boolean contiene(Posicion posicion) {
        return posicion.fila() >= 0 && posicion.fila() < alto()
                && posicion.columna() >= 0 && posicion.columna() < ancho();
    }

    public TipoCelda celdaEn(Posicion posicion) {
        return celdas[posicion.fila()][posicion.columna()];
    }

    TipoCelda[][] copiarCeldas() {
        return copiar(celdas);
    }

    void restaurarCeldas(TipoCelda[][] guardadas) {
        for (int fila = 0; fila < celdas.length; fila++) {
            celdas[fila] = guardadas[fila].clone();
        }
    }

    public int cantidadDeDestinos() {
        int destinos = 0;
        for (TipoCelda[] fila : celdas) {
            for (TipoCelda celda : fila) {
                if (celda == TipoCelda.DESTINO) {
                    destinos++;
                }
            }
        }
        return destinos;
    }

    public void abrirMuro(Posicion posicion) {
        if (contiene(posicion) && celdaEn(posicion) == TipoCelda.MURO_CERRADO) {
            celdas[posicion.fila()][posicion.columna()] = TipoCelda.MURO_ABIERTO;
        }
    }

    public ReglaDeMovimiento reglaEn(Posicion posicion) {
        return celdaEn(posicion).reglaDeMovimiento();
    }

    public boolean esTransitable(Posicion posicion) {
        return contiene(posicion) && celdaEn(posicion).esTransitable();
    }

    private static TipoCelda[][] copiar(TipoCelda[][] origen) {
        if (origen.length == 0 || origen[0].length == 0) {
            throw new IllegalArgumentException("El tablero no puede estar vacío");
        }
        TipoCelda[][] copia = new TipoCelda[origen.length][];
        for (int fila = 0; fila < origen.length; fila++) {
            if (origen[fila].length != origen[0].length) {
                throw new IllegalArgumentException("Todas las filas deben tener el mismo largo");
            }
            copia[fila] = origen[fila].clone();
        }
        return copia;
    }
}
