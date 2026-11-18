package sokoban.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import sokoban.controller.accion.Accion;
import sokoban.controller.accion.Continuar;
import sokoban.controller.accion.Deshacer;
import sokoban.controller.accion.IrANivel;
import sokoban.controller.accion.Mover;
import sokoban.controller.accion.Reiniciar;
import sokoban.exception.GuardadoInvalidoException;
import sokoban.model.enums.Direccion;

/**
 * Guarda una partida como la lista de acciones realizadas, una por línea, y la lee de vuelta.
 * Al cargar, esas acciones se reproducen sobre una partida nueva.
 */
public class GuardadoDePartida {

    static final String ENCABEZADO = "SOKOBAN-GUARDADO 1";
    static final String LINEA_DE_MODO = "MODO ";

    private static final Map<String, Function<String, Accion>> ACCIONES = Map.of(
            "MOVER", argumento -> new Mover(Direccion.valueOf(argumento)),
            "DESHACER", argumento -> new Deshacer(),
            "REINICIAR", argumento -> new Reiniciar(),
            "CONTINUAR", argumento -> new Continuar(),
            "IR_A_NIVEL", argumento -> new IrANivel(Integer.parseInt(argumento.trim())));

    public void guardar(List<Accion> acciones, Path ruta) throws IOException {
        guardar(null, acciones, ruta);
    }

    /** Igual que el anterior pero deja anotado el modo de juego, para volver a jugar con las mismas reglas. */
    public void guardar(String modo, List<Accion> acciones, Path ruta) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add(ENCABEZADO);
        if (modo != null) {
            lineas.add(LINEA_DE_MODO + modo);
        }
        acciones.forEach(accion -> lineas.add(accion.comoTexto()));
        Files.write(ruta, lineas, StandardCharsets.UTF_8);
    }

    public List<Accion> leer(Path ruta) throws IOException {
        List<String> lineas = leerLineas(ruta);
        List<Accion> acciones = new ArrayList<>();
        for (int i = esLineaDeModo(lineas) ? 2 : 1; i < lineas.size(); i++) {
            acciones.add(interpretar(lineas.get(i), i + 1));
        }
        return acciones;
    }

    /** Modo anotado en el archivo, o vacío si es un guardado sin modo (se juega como estaba la partida). */
    public Optional<String> leerModo(Path ruta) throws IOException {
        List<String> lineas = leerLineas(ruta);
        if (!esLineaDeModo(lineas)) {
            return Optional.empty();
        }
        return Optional.of(lineas.get(1).substring(LINEA_DE_MODO.length()).trim());
    }

    private List<String> leerLineas(Path ruta) throws IOException {
        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        if (lineas.isEmpty() || !lineas.get(0).equals(ENCABEZADO)) {
            throw new GuardadoInvalidoException("El archivo no es una partida guardada de Sokoban");
        }
        return lineas;
    }

    private boolean esLineaDeModo(List<String> lineas) {
        return lineas.size() > 1 && lineas.get(1).startsWith(LINEA_DE_MODO);
    }

    private Accion interpretar(String linea, int numeroDeLinea) {
        String[] partes = linea.trim().split(" ", 2);
        Function<String, Accion> crear = ACCIONES.get(partes[0]);
        if (crear == null) {
            throw new GuardadoInvalidoException("Acción desconocida en la línea " + numeroDeLinea + ": " + linea);
        }
        try {
            return crear.apply(partes.length > 1 ? partes[1] : "");
        } catch (IllegalArgumentException e) {
            throw new GuardadoInvalidoException("Argumento inválido en la línea " + numeroDeLinea + ": " + linea, e);
        }
    }
}
