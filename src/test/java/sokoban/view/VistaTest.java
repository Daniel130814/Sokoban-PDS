package sokoban.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                new RepositorioDeImagenes());
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
    void elHudMuestraLosContadoresDelNivel() {
        Partida partida = partida("#######", "#@  $.#", "#######");
        PanelHud hud = new PanelHud(partida, new ControladorDeJuego(partida), new Logros(partida, CatalogoDeLogros.estandar()));
        partida.agregarObservador(hud);

        partida.mover(Direccion.DERECHA);

        assertEquals("Nivel 1 | Movimientos: 1 | Empujes: 0", hud.textoDelNivel());
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
