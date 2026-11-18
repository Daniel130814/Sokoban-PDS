package sokoban.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sokoban.exception.GuardadoInvalidoException;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;
import sokoban.model.Posicion;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;
import sokoban.model.enums.EstadoPartida;
import sokoban.model.modo.ModoClasico;
import sokoban.model.modo.ModoDeJuego;
import sokoban.model.modo.ModoMovimientosLimitados;

class GuardadoYCargaTest {

    private static final List<String> NIVELES = List.of(
            "/levels/nivel1.txt", "/levels/nivel2.txt", "/levels/nivel3.txt", "/levels/nivel4.txt");

    @TempDir
    Path carpeta;

    private static Partida partidaNueva() {
        CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
        return new Partida(NIVELES.stream().map(cargador::cargarDesdeRecurso).toList());
    }

    private static void mover(ControladorDeJuego controlador, Direccion direccion, int veces) {
        for (int i = 0; i < veces; i++) {
            controlador.mover(direccion);
        }
    }

    @Test
    void unaPartidaEnCursoSeGuardaYSeRecuperaIgual() throws IOException {
        Partida original = partidaNueva();
        ControladorDeJuego controlador = new ControladorDeJuego(original);
        controlador.mover(Direccion.IZQUIERDA);
        controlador.continuar();
        mover(controlador, Direccion.DERECHA, 3);
        controlador.deshacer();
        mover(controlador, Direccion.DERECHA, 1);
        Path archivo = carpeta.resolve("partida.txt");
        controlador.guardar(archivo);

        Partida recuperada = partidaNueva();
        new ControladorDeJuego(recuperada).cargar(archivo);

        assertEquals(original.numeroDeNivel(), recuperada.numeroDeNivel());
        assertEquals(original.estado(), recuperada.estado());
        assertEquals(original.nivelActual().jugador().posicion(), recuperada.nivelActual().jugador().posicion());
        assertEquals(original.nivelActual().movimientos(), recuperada.nivelActual().movimientos());
        assertEquals(original.usosDeDeshacer(), recuperada.usosDeDeshacer());
        assertEquals(original.nivelActual().cajas().get(0).posicion(),
                recuperada.nivelActual().cajas().get(0).posicion());
    }

    @Test
    void cargarDescartaLoQueSeHabiaJugadoAntes() throws IOException {
        Partida partida = partidaNueva();
        ControladorDeJuego controlador = new ControladorDeJuego(partida);
        Path archivo = carpeta.resolve("partida.txt");
        controlador.mover(Direccion.IZQUIERDA);
        controlador.guardar(archivo);
        controlador.continuar();
        mover(controlador, Direccion.DERECHA, 4);

        controlador.cargar(archivo);

        assertEquals(1, partida.numeroDeNivel());
        assertEquals(EstadoPartida.NIVEL_COMPLETADO, partida.estado());
    }

    @Test
    void unaPartidaTerminadaSeRecuperaTerminada() throws IOException {
        Partida original = partidaNueva();
        ControladorDeJuego controlador = new ControladorDeJuego(original);
        controlador.mover(Direccion.IZQUIERDA);
        controlador.continuar();
        mover(controlador, Direccion.DERECHA, 5);
        controlador.continuar();
        mover(controlador, Direccion.DERECHA, 6);
        controlador.continuar();
        mover(controlador, Direccion.DERECHA, 3);
        controlador.continuar();
        assertEquals(EstadoPartida.JUEGO_TERMINADO, original.estado());
        Path archivo = carpeta.resolve("partida.txt");
        controlador.guardar(archivo);

        Partida recuperada = partidaNueva();
        new ControladorDeJuego(recuperada).cargar(archivo);

        assertEquals(EstadoPartida.JUEGO_TERMINADO, recuperada.estado());
    }

    @Test
    void soloSeRegistranLasAccionesQueTuvieronEfecto() throws IOException {
        Partida partida = partidaNueva();
        ControladorDeJuego controlador = new ControladorDeJuego(partida);
        controlador.deshacer();
        controlador.continuar();
        controlador.mover(Direccion.ABAJO);
        controlador.mover(Direccion.ABAJO);
        Path archivo = carpeta.resolve("partida.txt");

        controlador.guardar(archivo);

        assertEquals(List.of("SOKOBAN-GUARDADO 1", "MODO Clásico", "MOVER ABAJO"), Files.readAllLines(archivo));
    }

    @Test
    void elModoDeJuegoSeGuardaYSeRecuperaConLaPartida() throws IOException {
        List<ModoDeJuego> modos = List.of(new ModoClasico(), new ModoMovimientosLimitados(50));
        ControladorDeJuego controlador = new ControladorDeJuego(partidaNueva(), modos);
        controlador.elegirModo(modos.get(1));
        controlador.mover(Direccion.IZQUIERDA);
        Path archivo = carpeta.resolve("modo.txt");
        controlador.guardar(archivo);

        Partida recuperada = partidaNueva();
        new ControladorDeJuego(recuperada, modos).cargar(archivo);

        assertEquals("Movimientos limitados", recuperada.modo().nombre());
        assertEquals(1, recuperada.nivelActual().movimientos());
    }

    @Test
    void unGuardadoSinModoSeCargaConElModoActual() throws IOException {
        Path archivo = carpeta.resolve("viejo.txt");
        Files.write(archivo, List.of("SOKOBAN-GUARDADO 1", "MOVER IZQUIERDA"));
        Partida partida = partidaNueva();

        new ControladorDeJuego(partida).cargar(archivo);

        assertEquals("Clásico", partida.modo().nombre());
        assertEquals(1, partida.nivelActual().movimientos());
    }

    @Test
    void elNivelElegidoSeGuardaYAlCargarSeVuelveAlMismoNivel() throws IOException {
        Partida original = partidaNueva();
        ControladorDeJuego controlador = new ControladorDeJuego(original);
        controlador.irANivel(3);
        controlador.mover(Direccion.DERECHA);
        Path archivo = carpeta.resolve("nivel3.txt");
        controlador.guardar(archivo);

        Partida recuperada = partidaNueva();
        new ControladorDeJuego(recuperada).cargar(archivo);

        assertEquals(List.of("SOKOBAN-GUARDADO 1", "MODO Clásico", "IR_A_NIVEL 3", "MOVER DERECHA"),
                Files.readAllLines(archivo));
        assertEquals(3, recuperada.numeroDeNivel());
        assertEquals(1, recuperada.nivelActual().movimientos());
    }

    @Test
    void unNumeroDeNivelInvalidoEnElArchivoSeRechaza() throws IOException {
        Path archivo = carpeta.resolve("raro.txt");
        Files.write(archivo, List.of("SOKOBAN-GUARDADO 1", "IR_A_NIVEL tres"));

        assertThrows(GuardadoInvalidoException.class,
                () -> new ControladorDeJuego(partidaNueva()).cargar(archivo));
    }

    @Test
    void unModoDesconocidoEnElArchivoSeRechazaSinTocarLaPartida() throws IOException {
        Path archivo = carpeta.resolve("raro.txt");
        Files.write(archivo, List.of("SOKOBAN-GUARDADO 1", "MODO Inventado", "MOVER IZQUIERDA"));
        Partida partida = partidaNueva();

        assertThrows(GuardadoInvalidoException.class, () -> new ControladorDeJuego(partida).cargar(archivo));

        assertEquals(0, partida.nivelActual().movimientos());
    }

    @Test
    void cargarAvisaUnaSolaVezQueElNivelCambio() throws IOException {
        Partida partida = partidaNueva();
        ControladorDeJuego controlador = new ControladorDeJuego(partida);
        controlador.mover(Direccion.IZQUIERDA);
        controlador.continuar();
        Path archivo = carpeta.resolve("partida.txt");
        controlador.guardar(archivo);
        List<String> eventos = new ArrayList<>();
        partida.agregarObservador(new ObservadorDeJuego() {
            @Override
            public void nivelCambiado() {
                eventos.add("nivelCambiado");
            }

            @Override
            public void jugadorSeMovio() {
                eventos.add("jugadorSeMovio");
            }

            @Override
            public void nivelCompletado() {
                eventos.add("nivelCompletado");
            }
        });

        controlador.cargar(archivo);

        assertEquals(List.of("nivelCambiado"), eventos);
    }

    @Test
    void unArchivoQueNoEsUnGuardadoSeRechaza() throws IOException {
        Path archivo = carpeta.resolve("otro.txt");
        Files.writeString(archivo, "hola\nmundo\n");
        ControladorDeJuego controlador = new ControladorDeJuego(partidaNueva());

        assertThrows(GuardadoInvalidoException.class, () -> controlador.cargar(archivo));
    }

    @Test
    void unaAccionDesconocidaOUnArgumentoInvalidoSeRechazan() throws IOException {
        ControladorDeJuego controlador = new ControladorDeJuego(partidaNueva());
        Path desconocida = carpeta.resolve("desconocida.txt");
        Files.write(desconocida, List.of("SOKOBAN-GUARDADO 1", "SALTAR"));
        Path invalida = carpeta.resolve("invalida.txt");
        Files.write(invalida, List.of("SOKOBAN-GUARDADO 1", "MOVER DIAGONAL"));

        assertThrows(GuardadoInvalidoException.class, () -> controlador.cargar(desconocida));
        assertThrows(GuardadoInvalidoException.class, () -> controlador.cargar(invalida));
    }

    @Test
    void unaCargaFallidaNoAlteraLaPartidaEnCurso() throws IOException {
        Partida partida = partidaNueva();
        ControladorDeJuego controlador = new ControladorDeJuego(partida);
        controlador.mover(Direccion.ABAJO);
        Posicion antes = partida.nivelActual().jugador().posicion();
        Path invalida = carpeta.resolve("invalida.txt");
        Files.write(invalida, List.of("SOKOBAN-GUARDADO 1", "SALTAR"));

        assertThrows(GuardadoInvalidoException.class, () -> controlador.cargar(invalida));

        assertEquals(antes, partida.nivelActual().jugador().posicion());
        assertTrue(partida.nivelActual().movimientos() > 0);
    }
}
