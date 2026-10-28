package sokoban.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import sokoban.controller.ControladorDeJuego;
import sokoban.logros.Logros;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;

/**
 * Ventana con una tarjeta por pantalla (menú, juego, nivel completado y fin). Cambia de tarjeta
 * según los eventos del modelo; no decide nada del juego.
 */
public class VentanaPrincipal extends JFrame implements ObservadorDeJuego {

    static final String MENU = "menu";
    static final String JUEGO = "juego";
    static final String COMPLETADO = "completado";
    static final String FIN = "fin";

    private final Partida partida;
    private final ControladorDeJuego controlador;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final PanelDeTablero tablero;
    private final PantallaNivelCompletado pantallaCompletado;

    public VentanaPrincipal(Partida partida, ControladorDeJuego controlador, RepositorioDeImagenes imagenes,
            RepositorioDeFuentes fuentes, Logros logros) {
        super("Sokoban - Los Desinformaticos");
        this.partida = partida;
        this.controlador = controlador;

        tablero = new PanelDeTablero(partida, controlador, imagenes);
        PanelHud hud = new PanelHud(partida, controlador, logros, imagenes, fuentes);
        partida.agregarObservador(tablero);
        partida.agregarObservador(hud);
        partida.agregarObservador(this);

        PanelEspacial juego = new PanelEspacial(imagenes, "fondo_espacio");
        juego.setLayout(new BorderLayout());
        PanelEspacial centro = new PanelEspacial(imagenes, "fondo_espacio");
        centro.setLayout(new GridBagLayout());
        centro.add(tablero);
        juego.add(hud, BorderLayout.NORTH);
        juego.add(centro, BorderLayout.CENTER);

        Runnable cargar = () -> {
            if (hud.cargar()) {
                mostrar(JUEGO);
            }
        };
        pantallaCompletado = new PantallaNivelCompletado(imagenes, fuentes, controlador::continuar);
        contenido.add(new PantallaDeMenu(imagenes, fuentes, () -> mostrar(JUEGO), cargar, this::salir), MENU);
        contenido.add(juego, JUEGO);
        contenido.add(pantallaCompletado, COMPLETADO);
        contenido.add(new PantallaFinDeJuego(imagenes, fuentes, cargar, this::salir), FIN);

        setContentPane(contenido);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void salir() {
        dispose();
        System.exit(0);
    }

    void mostrar(String tarjeta) {
        tarjetas.show(contenido, tarjeta);
        if (JUEGO.equals(tarjeta)) {
            tablero.requestFocusInWindow();
        }
    }

    @Override
    public void nivelCompletado() {
        SwingUtilities.invokeLater(() -> {
            pantallaCompletado.mostrar(partida.resumenDelNivel());
            mostrar(COMPLETADO);
        });
    }

    @Override
    public void cajaSeRompio() {
        if (!partida.nivelActual().quedoIrresoluble()) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "Se rompió una caja y el nivel ya no se puede resolver.\n¿Reiniciar el nivel?",
                    "Nivel sin solución", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                controlador.reiniciarNivel();
            }
        });
    }

    @Override
    public void nivelCambiado() {
        pack();
        mostrar(JUEGO);
    }

    @Override
    public void juegoTerminado() {
        SwingUtilities.invokeLater(() -> mostrar(FIN));
    }
}
