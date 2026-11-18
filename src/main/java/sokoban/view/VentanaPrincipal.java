package sokoban.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import sokoban.controller.ControladorDeJuego;
import sokoban.logros.Logros;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;
import sokoban.model.campania.ElementoDeCampania;
import sokoban.model.modo.ModoDeJuego;
import sokoban.tiempo.Cronometro;

/**
 * Ventana con una tarjeta por pantalla (menú, juego, nivel completado y fin). Cambia de tarjeta
 * según los eventos del modelo; no decide nada del juego.
 */
public class VentanaPrincipal extends JFrame implements ObservadorDeJuego {

    static final String MENU = "menu";
    static final String JUEGO = "juego";
    static final String COMPLETADO = "completado";
    static final String FIN = "fin";
    static final String LOGROS = "logros";
    static final String NIVELES = "niveles";
    static final String MODOS = "modos";
    static final String FALLIDO = "fallido";

    private final Partida partida;
    private final ControladorDeJuego controlador;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final PanelDeTablero tablero;
    private final PantallaNivelCompletado pantallaCompletado;
    private final PantallaNivelFallido pantallaFallida;
    private final Cronometro cronometro;

    public VentanaPrincipal(Partida partida, ControladorDeJuego controlador, RecursosVisuales recursos,
            Logros logros, ElementoDeCampania campania, Cronometro cronometro,
            List<ModoDeJuego> modos) {
        super("Sokoban - Los Desinformaticos");
        this.partida = partida;
        this.controlador = controlador;
        this.cronometro = cronometro;

        tablero = new PanelDeTablero(partida, controlador, recursos);
        PanelHud hud = new PanelHud(partida, controlador, logros, recursos, cronometro, () -> mostrar(LOGROS),
                this::confirmarVolverAlInicio);
        partida.agregarObservador(tablero);
        partida.agregarObservador(hud);
        partida.agregarObservador(this);

        PanelEspacial juego = new PanelEspacial(recursos, "fondo_espacio");
        juego.setLayout(new BorderLayout());
        PanelEspacial centro = new PanelEspacial(recursos, "fondo_espacio");
        centro.setLayout(new GridBagLayout());
        centro.add(tablero);
        juego.add(hud, BorderLayout.NORTH);
        juego.add(centro, BorderLayout.CENTER);

        Runnable cargar = () -> {
            if (hud.cargar()) {
                mostrar(JUEGO);
            }
        };
        pantallaCompletado = new PantallaNivelCompletado(recursos, controlador::continuar);
        contenido.add(new PantallaDeMenu(recursos, () -> mostrar(MODOS), () -> mostrar(NIVELES), cargar,
                this::salir), MENU);
        contenido.add(new PantallaDeNiveles(recursos, campania, () -> mostrar(MENU)), NIVELES);
        contenido.add(juego, JUEGO);
        contenido.add(new PantallaDeModos(recursos, modos, controlador::elegirModo, () -> mostrar(MENU)), MODOS);
        pantallaFallida = new PantallaNivelFallido(recursos, controlador::reiniciarNivel, this::volverAlInicio);
        contenido.add(pantallaFallida, FALLIDO);
        contenido.add(new PantallaDeLogros(recursos, logros, () -> mostrar(JUEGO)), LOGROS);
        contenido.add(pantallaCompletado, COMPLETADO);
        contenido.add(new PantallaFinDeJuego(recursos, this::volverAlInicio, this::salir), FIN);

        setContentPane(contenido);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
    }

    /** Desde el nivel: pregunta antes de salir, porque volver al inicio descarta lo que no se guardó. */
    private void confirmarVolverAlInicio() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Volver al inicio?
Se pierde el progreso que no hayas guardado.",
                "Volver al inicio", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            volverAlInicio();
        }
    }

    /** Empieza de nuevo desde el primer nivel y muestra el menú (el reinicio avisa nivelCambiado, que va al juego). */
    private void volverAlInicio() {
        controlador.nuevaPartida();
        mostrar(MENU);
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
            pantallaCompletado.mostrar(partida.resumenDelNivel(), cronometro.comoTexto());
            mostrar(COMPLETADO);
        });
    }

    @Override
    public void nivelFallido() {
        SwingUtilities.invokeLater(() -> {
            pantallaFallida.mostrar(partida.modo().nombre(), partida.modo().descripcion());
            mostrar(FALLIDO);
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
        mostrar(JUEGO);
    }

    @Override
    public void juegoTerminado() {
        SwingUtilities.invokeLater(() -> mostrar(FIN));
    }
}
