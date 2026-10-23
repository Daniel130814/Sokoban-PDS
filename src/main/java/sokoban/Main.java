package sokoban;

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
import sokoban.view.RepositorioDeImagenes;
import sokoban.view.VentanaPrincipal;

public class Main {

    private static final List<String> NIVELES = List.of(
            "/levels/nivel1.txt",
            "/levels/nivel2.txt",
            "/levels/nivel3.txt",
            "/levels/nivel4.txt");

    public static void main(String[] args) {
        Partida partida;
        try {
            CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
            List<Nivel> niveles = NIVELES.stream().map(cargador::cargarDesdeRecurso).toList();
            partida = new Partida(niveles, new PuntajePorEficiencia());
        } catch (NivelInvalidoException e) {
            System.err.println("No se pudieron cargar los niveles: " + e.getMessage());
            return;
        }
        partida.agregarObservador(new SonidosDelJuego(new ReproductorJavaSound()));
        Logros logros = new Logros(partida, CatalogoDeLogros.estandar());
        partida.agregarObservador(logros);
        ControladorDeJuego controlador = new ControladorDeJuego(partida);
        SwingUtilities.invokeLater(() ->
                new VentanaPrincipal(partida, controlador, new RepositorioDeImagenes(), logros).setVisible(true));
    }
}
