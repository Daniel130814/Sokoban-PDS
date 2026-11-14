package sokoban.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import sokoban.controller.ControladorDeJuego;
import sokoban.logros.CatalogoDeLogros;
import sokoban.logros.Logros;
import sokoban.model.CajaFragil;
import sokoban.model.CajaLlave;
import sokoban.model.CajaNormal;
import sokoban.model.Partida;
import sokoban.model.Posicion;
import sokoban.model.TipoCelda;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class VistaTest {

    private static Partida partida(String... filas) {
        return new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos()).cargar(List.of(filas))));
    }

    @Test
    void elRepositorioCargaCadaImagenUnaSolaVezYLaComparte() {
        RepositorioDeImagenes repositorio = new RepositorioDeImagenes();

        BufferedImage primera = repositorio.imagen("pared");
        BufferedImage segunda = repositorio.imagen("pared");

        assertSame(primera, segunda);
        assertEquals(1, repositorio.cantidadCargadas());
    }

    private static List<javax.swing.JButton> botones(java.awt.Container contenedor) {
        List<javax.swing.JButton> botones = new java.util.ArrayList<>();
        for (java.awt.Component hijo : contenedor.getComponents()) {
            if (hijo instanceof javax.swing.JButton boton) {
                botones.add(boton);
            } else if (hijo instanceof java.awt.Container interior) {
                botones.addAll(botones(interior));
            }
        }
        return botones;
    }

    @Test
    void laPantallaDeModosTieneUnBotonPorModoYAlElegirUnoLoEntrega() {
        List<sokoban.model.modo.ModoDeJuego> modos = List.of(new sokoban.model.modo.ModoClasico(),
                new sokoban.model.modo.ModoMovimientosLimitados());
        List<String> elegidos = new java.util.ArrayList<>();
        PantallaDeModos pantalla = new PantallaDeModos(new RecursosVisuales(), modos,
                modo -> elegidos.add(modo.nombre()), () -> { });

        List<javax.swing.JButton> botones = botones(pantalla);
        botones.get(1).doClick();

        assertEquals(3, botones.size());
        assertEquals(List.of("Movimientos limitados"), elegidos);
    }

    @Test
    void laPantallaDeNivelFallidoOfreceReintentarYVolverAlMenu() {
        List<String> acciones = new java.util.ArrayList<>();
        PantallaNivelFallido pantalla = new PantallaNivelFallido(new RecursosVisuales(),
                () -> acciones.add("reintentar"), () -> acciones.add("menu"));
        pantalla.mostrar("Contrarreloj", "Se acabó el tiempo");

        List<javax.swing.JButton> botones = botones(pantalla);
        botones.get(0).doClick();
        botones.get(1).doClick();

        assertEquals(List.of("reintentar", "menu"), acciones);
    }

    @Test
    void elCatalogoDeNivelesSeDibujaConLosMundosDeLaCampania() {
        CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
        var campania = sokoban.model.campania.CampaniaEstandar.crear(cargador::cargarDesdeRecurso);

        PantallaDeNiveles pantalla = new PantallaDeNiveles(new RecursosVisuales(), campania, () -> { });
        pantalla.setSize(1200, 800);
        pantalla.doLayout();
        BufferedImage imagen = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        pantalla.paint(g);
        g.dispose();

        assertEquals(1, botones(pantalla).stream().filter(BotonEspacial.class::isInstance).count());
    }

    @Test
    void laFachadaEntregaImagenesYFuentesSinExponerLosRepositorios() {
        RecursosVisuales recursos = new RecursosVisuales();

        assertSame(recursos.imagen("pared"), recursos.imagen("pared"));
        assertEquals(20f, recursos.fuente("kenvector_future", 20f).getSize2D());
    }

    @Test
    void unaImagenInexistenteFallaConUnMensajeClaro() {
        assertThrows(IllegalStateException.class, () -> new RepositorioDeImagenes().imagen("no-existe"));
    }

    @Test
    void todosLosElementosDelModeloTienenImagen() {
        SelectorDeSprites selector = new SelectorDeSprites();
        RepositorioDeImagenes repositorio = new RepositorioDeImagenes();

        for (TipoCelda celda : TipoCelda.values()) {
            repositorio.imagen(selector.paraCelda(celda));
        }
        repositorio.imagen(selector.paraCaja(new CajaNormal(new Posicion(0, 0))));
        repositorio.imagen(selector.paraCaja(new CajaFragil(new Posicion(0, 0))));
        repositorio.imagen(selector.paraCaja(new CajaLlave(new Posicion(0, 0), 1)));
        repositorio.imagen(selector.paraJugador());

        assertEquals(TipoCelda.values().length + 4, repositorio.cantidadCargadas());
    }

    @Test
    void elPanelDibujaElTableroYSeActualizaAlMoverse() throws IOException {
        Partida partida = partida("#########", "#@ $~~ .#", "#########");
        PanelDeTablero panel = new PanelDeTablero(partida, new ControladorDeJuego(partida),
                new RecursosVisuales());
        partida.agregarObservador(panel);
        panel.setSize(panel.getPreferredSize());

        BufferedImage antes = captura(panel);
        partida.mover(Direccion.DERECHA);
        BufferedImage despues = captura(panel);

        assertEquals(9 * PanelDeTablero.TAMANIO_CELDA, antes.getWidth());
        assertNotEquals(pixel(antes, 1, 2), pixel(despues, 1, 2));
        File carpeta = new File("target");
        carpeta.mkdirs();
        ImageIO.write(antes, "png", new File(carpeta, "captura-nivel2.png"));
    }

    @Test
    void elHudMuestraElRelojSegunElModo() {
        long[] ahora = {0};
        sokoban.tiempo.Cronometro cronometro = new sokoban.tiempo.Cronometro(() -> ahora[0]);
        Partida partida = new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos())
                .cargar(List.of("#######", "#@  $.#", "#######"))), new sokoban.model.puntaje.PuntajePorEficiencia(),
                new sokoban.tiempo.ModoContrarreloj(cronometro, java.time.Duration.ofSeconds(60)));
        partida.agregarObservador(cronometro);
        PanelHud hud = new PanelHud(partida, new ControladorDeJuego(partida),
                new Logros(partida, CatalogoDeLogros.estandar()), new RecursosVisuales(), cronometro, () -> { });

        assertEquals("Quedan 01:00", hud.textoDeTiempo());
        ahora[0] = 1_000;
        partida.mover(Direccion.DERECHA);
        ahora[0] = 21_000;
        assertEquals("Quedan 00:40", hud.textoDeTiempo());

        partida.establecerModo(new sokoban.model.modo.ModoClasico());
        assertEquals("Tiempo 00:20", hud.textoDeTiempo());
    }

    @Test
    void elHudMuestraLosContadoresDelNivel() {
        Partida partida = partida("#######", "#@  $.#", "#######");
        PanelHud hud = new PanelHud(partida, new ControladorDeJuego(partida),
                new Logros(partida, CatalogoDeLogros.estandar()), new RecursosVisuales(), new sokoban.tiempo.Cronometro(), () -> { });
        partida.agregarObservador(hud);

        partida.mover(Direccion.DERECHA);

        assertEquals("Nivel 1 | Movimientos: 1 | Empujes: 0", hud.textoDelNivel());
    }

    @Test
    void elRepositorioDeFuentesCargaUnaVezYUsaRespaldoSiFalta() {
        RepositorioDeFuentes fuentes = new RepositorioDeFuentes();

        Font principal = fuentes.fuente("kenvector_future", 20f);
        Font respaldo = fuentes.fuente("no-existe", 12f);

        assertEquals(20f, principal.getSize2D());
        assertEquals("KenVector Future", principal.getFamily());
        assertNotNull(respaldo);
        fuentes.fuente("kenvector_future", 30f);
        assertEquals(2, fuentes.cantidadCargadas());
    }

    @Test
    void elDestelloSeApagaDespuesDeSusCuadros() {
        EfectoDeDestello destello = new EfectoDeDestello(() -> { });

        destello.disparar(Color.RED);
        assertTrue(destello.activo());
        for (int i = 0; i < EfectoDeDestello.CUADROS; i++) {
            destello.paso();
        }

        assertFalse(destello.activo());
    }

    @Test
    void elTableroSeDestellaAlRomperseUnaCajaYAlActivarseUnCerrojo() {
        Partida partida = partida("#######", "#@%  .#", "#######");
        PanelDeTablero panel = new PanelDeTablero(partida, new ControladorDeJuego(partida),
                new RecursosVisuales());
        panel.setSize(panel.getPreferredSize());
        BufferedImage normal = captura(panel);

        panel.cajaSeRompio();
        BufferedImage conDestello = captura(panel);

        assertNotEquals(normal.getRGB(2, 2), conDestello.getRGB(2, 2));
    }

    private static BufferedImage captura(PanelDeTablero panel) {
        BufferedImage imagen = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        panel.paint(g);
        g.dispose();
        return imagen;
    }

    private static int pixel(BufferedImage imagen, int fila, int columna) {
        int medio = PanelDeTablero.TAMANIO_CELDA / 2;
        return imagen.getRGB(columna * PanelDeTablero.TAMANIO_CELDA + medio + 6,
                fila * PanelDeTablero.TAMANIO_CELDA + medio - 4);
    }
}
