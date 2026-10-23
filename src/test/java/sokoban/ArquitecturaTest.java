package sokoban;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Hace cumplir DD-001: las capas que no son interfaz no importan Swing, AWT ni javax.sound. */
class ArquitecturaTest {

    private static final List<String> CAPAS_SIN_INTERFAZ = List.of("model", "controller", "logros", "persistencia");
    private static final List<String> PROHIBIDOS = List.of("javax.swing", "java.awt", "javax.sound");

    @Test
    void elModeloYLosControladoresNoDependenDeLaInterfazGrafica() throws IOException {
        for (String capa : CAPAS_SIN_INTERFAZ) {
            Path carpeta = Path.of("src/main/java/sokoban", capa);
            assertTrue(Files.isDirectory(carpeta), "No existe " + carpeta);
            try (Stream<Path> archivos = Files.walk(carpeta)) {
                for (Path archivo : archivos.filter(ruta -> ruta.toString().endsWith(".java")).toList()) {
                    String contenido = Files.readString(archivo);
                    for (String prohibido : PROHIBIDOS) {
                        assertTrue(!contenido.contains("import " + prohibido),
                                archivo + " importa " + prohibido);
                    }
                }
            }
        }
    }
}
