package sokoban.model.carga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sokoban.exception.NivelInvalidoException;
import sokoban.model.Caja;
import sokoban.model.Cerrojo;
import sokoban.model.Jugador;
import sokoban.model.Muro;
import sokoban.model.Nivel;
import sokoban.model.Posicion;
import sokoban.model.Tablero;
import sokoban.model.TipoCelda;

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
        Lectura lectura = new Lectura(lineas);
        for (int fila = 0; fila < lineas.size(); fila++) {
            String linea = lineas.get(fila);
            for (int columna = 0; columna < linea.length(); columna++) {
                leerCaracter(lectura, linea.charAt(columna), new Posicion(fila, columna));
            }
        }
        validar(lectura);

        Tablero tablero = new Tablero(lectura.celdas);
        conectarMuros(lectura, tablero);
        return new Nivel(tablero, new Jugador(lectura.inicioJugador), lectura.cajas, lectura.cerrojos);
    }

    private void leerCaracter(Lectura lectura, char simbolo, Posicion posicion) {
        TipoCelda celda = fabrica.crearCelda(simbolo);
        lectura.celdas[posicion.fila()][posicion.columna()] = celda;
        if (celda == TipoCelda.DESTINO) {
            lectura.destinos++;
        }
        fabrica.crearCaja(simbolo, posicion).ifPresent(lectura.cajas::add);
        fabrica.idDeCerrojo(simbolo).ifPresent(id -> lectura.cerrojos.add(new Cerrojo(id, posicion)));
        fabrica.idDeMuro(simbolo).ifPresent(id ->
                lectura.murosPorId.computeIfAbsent(id, clave -> new ArrayList<>()).add(posicion));
        if (fabrica.esJugador(simbolo)) {
            if (lectura.inicioJugador != null) {
                throw new NivelInvalidoException(
                        "El nivel tiene más de un jugador (fila " + posicion.fila() + ")");
            }
            lectura.inicioJugador = posicion;
        }
    }

    private void conectarMuros(Lectura lectura, Tablero tablero) {
        for (Cerrojo cerrojo : lectura.cerrojos) {
            for (Posicion posicionMuro : lectura.murosPorId.getOrDefault(cerrojo.id(), List.of())) {
                cerrojo.agregarObservador(new Muro(tablero, posicionMuro));
            }
        }
    }

    private void validar(Lectura lectura) {
        if (lectura.inicioJugador == null) {
            throw new NivelInvalidoException("El nivel no tiene jugador");
        }
        if (lectura.cajas.isEmpty() || lectura.cajas.size() != lectura.destinos) {
            throw new NivelInvalidoException(
                    "Debe haber la misma cantidad de cajas y destinos (cajas: " + lectura.cajas.size()
                            + ", destinos: " + lectura.destinos + ")");
        }
    }

    /** Datos acumulados mientras se recorre el archivo. */
    private static final class Lectura {
        final TipoCelda[][] celdas;
        final List<Caja> cajas = new ArrayList<>();
        final List<Cerrojo> cerrojos = new ArrayList<>();
        final Map<Integer, List<Posicion>> murosPorId = new HashMap<>();
        Posicion inicioJugador;
        int destinos;

        Lectura(List<String> lineas) {
            int ancho = lineas.stream().mapToInt(String::length).max().orElse(0);
            celdas = new TipoCelda[lineas.size()][ancho];
            for (TipoCelda[] fila : celdas) {
                Arrays.fill(fila, TipoCelda.PARED);
            }
        }
    }
}
