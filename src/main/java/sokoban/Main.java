package sokoban;

import com.formdev.flatlaf.FlatDarkLaf;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import sokoban.audio.ReproductorJavaSound;
import sokoban.audio.SonidosDelJuego;
import sokoban.controller.ControladorDeJuego;
import sokoban.exception.NivelInvalidoException;
import sokoban.logros.CatalogoDeLogros;
import sokoban.logros.Logros;
import sokoban.model.Nivel;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.puntaje.PuntajePorEficiencia;
import sokoban.tiempo.Cronometro;
import sokoban.view.FichaDeNivel;
import sokoban.view.RepositorioDeFuentes;
import sokoban.view.RepositorioDeImagenes;
import sokoban.view.VentanaPrincipal;

public class Main {

    /** Recurso, título y detalle de cada nivel; los primeros cuatro son el tutorial. */
    private record DatosDeNivel(String ruta, String titulo, String detalle) {
    }

    private static final List<DatosDeNivel> NIVELES = List.of(
            new DatosDeNivel("/levels/nivel1.txt", "Tutorial 1", "Empujá la caja hasta el destino"),
            new DatosDeNivel("/levels/nivel2.txt", "Tutorial 2", "Hielo: la caja se desliza"),
            new DatosDeNivel("/levels/nivel3.txt", "Tutorial 3", "Caja llave, cerrojo y muro"),
            new DatosDeNivel("/levels/nivel4.txt", "Tutorial 4", "Caja frágil: se rompe al empujarla"),
            new DatosDeNivel("/levels/nivel5.txt", "Nivel 5", "Tres cajas, tres destinos"),
            new DatosDeNivel("/levels/nivel6.txt", "Nivel 6", "Tres cajas en fila"),
            new DatosDeNivel("/levels/nivel7.txt", "Nivel 7", "Dos pasillos, tres cajas"),
            new DatosDeNivel("/levels/nivel8.txt", "Nivel 8", "Ocho cajas: el desafío final"));

    public static void main(String[] args) {
        Partida partida;
        List<FichaDeNivel> fichas = new ArrayList<>();
        try {
            CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
            List<Nivel> niveles = NIVELES.stream().map(datos -> cargador.cargarDesdeRecurso(datos.ruta())).toList();
            partida = new Partida(niveles, new PuntajePorEficiencia());
            // Copias sin jugar, solo para mostrar el catálogo de niveles.
            for (DatosDeNivel datos : NIVELES) {
                fichas.add(new FichaDeNivel(datos.titulo(), datos.detalle(), cargador.cargarDesdeRecurso(datos.ruta())));
            }
        } catch (NivelInvalidoException e) {
            System.err.println("No se pudieron cargar los niveles: " + e.getMessage());
            return;
        }
        partida.agregarObservador(new SonidosDelJuego(new ReproductorJavaSound()));
        Logros logros = new Logros(partida, CatalogoDeLogros.estandar());
        partida.agregarObservador(logros);
        ControladorDeJuego controlador = new ControladorDeJuego(partida);
        FlatDarkLaf.setup();
        Cronometro cronometro = new Cronometro();
        partida.agregarObservador(cronometro);
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(partida, controlador, new RepositorioDeImagenes(),
                new RepositorioDeFuentes(), logros, fichas, cronometro).setVisible(true));
    }
}
