package sokoban;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingUtilities;
import sokoban.audio.ReproductorJavaSound;
import sokoban.audio.SonidosDelJuego;
import sokoban.controller.ControladorDeJuego;
import sokoban.exception.NivelInvalidoException;
import sokoban.logros.CatalogoDeLogros;
import sokoban.logros.Logros;
import java.util.List;
import sokoban.model.Partida;
import sokoban.model.campania.CampaniaEstandar;
import sokoban.model.campania.Mundo;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.modo.ModoClasico;
import sokoban.model.modo.ModoDeJuego;
import sokoban.model.modo.ModoMovimientosLimitados;
import sokoban.model.puntaje.PuntajePorEficiencia;
import sokoban.tiempo.Cronometro;
import sokoban.tiempo.ModoContrarreloj;
import sokoban.view.RecursosVisuales;
import sokoban.view.VentanaPrincipal;

public class Main {

    public static void main(String[] args) {
        Partida partida;
        Mundo catalogo;
        try {
            CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());
            partida = new Partida(CampaniaEstandar.crear(cargador::cargarDesdeRecurso).niveles(),
                    new PuntajePorEficiencia());
            // Segunda carga, sin jugar: el catálogo muestra los niveles tal como empiezan.
            catalogo = CampaniaEstandar.crear(cargador::cargarDesdeRecurso);
        } catch (NivelInvalidoException e) {
            System.err.println("No se pudieron cargar los niveles: " + e.getMessage());
            return;
        }
        partida.agregarObservador(new SonidosDelJuego(new ReproductorJavaSound()));
        Logros logros = new Logros(partida, CatalogoDeLogros.estandar());
        partida.agregarObservador(logros);
        Cronometro cronometro = new Cronometro();
        partida.agregarObservador(cronometro);
        List<ModoDeJuego> modos = List.of(new ModoClasico(), new ModoContrarreloj(cronometro),
                new ModoMovimientosLimitados());
        ControladorDeJuego controlador = new ControladorDeJuego(partida, modos);
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(partida, controlador, new RecursosVisuales(),
                logros, catalogo, cronometro, modos).setVisible(true));
    }
}
