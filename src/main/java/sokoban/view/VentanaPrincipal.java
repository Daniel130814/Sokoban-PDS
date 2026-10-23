package sokoban.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import sokoban.controller.ControladorDeJuego;
import sokoban.model.Nivel;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;

public class VentanaPrincipal extends JFrame implements ObservadorDeJuego {

    private final Partida partida;
    private final ControladorDeJuego controlador;

    public VentanaPrincipal(Partida partida, ControladorDeJuego controlador, RepositorioDeImagenes imagenes) {
        super("Sokoban - Los Desinformaticos");
        this.partida = partida;
        this.controlador = controlador;

        PanelDeTablero tablero = new PanelDeTablero(partida, controlador, imagenes);
        PanelHud hud = new PanelHud(partida, controlador);
        partida.agregarObservador(tablero);
        partida.agregarObservador(hud);
        partida.agregarObservador(this);

        setLayout(new BorderLayout());
        add(hud, BorderLayout.NORTH);
        add(tablero, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void nivelCompletado() {
        Nivel nivel = partida.nivelActual();
        String resumen = "Nivel completado\n\nMovimientos: " + nivel.movimientos()
                + "\nEmpujes: " + nivel.empujes()
                + "\nDeshacer usado: " + partida.usosDeDeshacer() + " veces";
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, resumen, "Resumen del nivel", JOptionPane.INFORMATION_MESSAGE);
            controlador.continuar();
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
    }

    @Override
    public void juegoTerminado() {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                "¡Completaste todos los niveles!", "Fin del juego", JOptionPane.INFORMATION_MESSAGE));
    }
}
