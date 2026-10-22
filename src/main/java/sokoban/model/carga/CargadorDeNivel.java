package sokoban.model.carga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sokoban.exception.NivelInvalidoException;
import sokoban.model.Caja;
import sokoban.model.Jugador;
import sokoban.model.Nivel;
import sokoban.model.Posicion;
import sokoban.model.Tablero;
import sokoban.model.enums.TipoCelda;

public class CargadorDeNivel {

    private final FabricaDeElementos fabrica;

    public CargadorDeNivel(FabricaDeElementos fabrica) {
        this.fabrica = fabrica;
    }

    public Nivel cargarDesdeRecurso(String ruta) {
        try (InputStream entrada = CargadorDeNivel.class.getResourceAsStream(ruta)) {
            if (entrada == null) {
                throw new NivelInvalidoException("No existe el recurso de nivel: " + ruta);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8));
            return cargar(lector.lines().toList());
        } catch (IOException e) {
            throw new NivelInvalidoException("No se pudo leer el nivel: " + ruta, e);
        }
    }

    public Nivel cargar(List<String> lineas) {
        if (lineas.isEmpty()) {
            throw new NivelInvalidoException("El nivel está vacío");
        }
        int ancho = lineas.stream().mapToInt(String::length).max().orElse(0);
        TipoCelda[][] celdas = new TipoCelda[lineas.size()][ancho];
        List<Caja> cajas = new ArrayList<>();
        Posicion inicioJugador = null;
        int destinos = 0;

        for (int fila = 0; fila < lineas.size(); fila++) {
            Arrays.fill(celdas[fila], TipoCelda.PARED);
            String linea = lineas.get(fila);
            for (int columna = 0; columna < linea.length(); columna++) {
                char simbolo = linea.charAt(columna);
                Posicion posicion = new Posicion(fila, columna);
                celdas[fila][columna] = fabrica.crearCelda(simbolo);
                if (celdas[fila][columna] == TipoCelda.DESTINO) {
                    destinos++;
                }
                fabrica.crearCaja(simbolo, posicion).ifPresent(cajas::add);
                if (fabrica.esJugador(simbolo)) {
                    if (inicioJugador != null) {
                        throw new NivelInvalidoException("El nivel tiene más de un jugador (fila " + fila + ")");
                    }
                    inicioJugador = posicion;
                }
            }
        }

        validar(inicioJugador, cajas.size(), destinos);
        return new Nivel(new Tablero(celdas), new Jugador(inicioJugador), cajas);
    }

    private void validar(Posicion inicioJugador, int cantidadCajas, int cantidadDestinos) {
        if (inicioJugador == null) {
            throw new NivelInvalidoException("El nivel no tiene jugador");
        }
        if (cantidadCajas == 0 || cantidadCajas != cantidadDestinos) {
            throw new NivelInvalidoException(
                    "Debe haber la misma cantidad de cajas y destinos (cajas: " + cantidadCajas
                            + ", destinos: " + cantidadDestinos + ")");
        }
    }
}
