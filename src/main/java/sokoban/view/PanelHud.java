package sokoban.view;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sokoban.controller.ControladorDeJuego;
import sokoban.model.Nivel;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;

/** HUD: muestra nivel, movimientos y empujes, y ofrece los botones de deshacer y reiniciar. */
public class PanelHud extends JPanel implements ObservadorDeJuego {

    private final Partida partida;
    private final JLabel nivel = new JLabel();
    private final JLabel movimientos = new JLabel();
    private final JLabel empujes = new JLabel();
    private final JLabel deshacer = new JLabel();

    public PanelHud(Partida partida, ControladorDeJuego controlador) {
        super(new FlowLayout(FlowLayout.LEFT, 16, 6));
        this.partida = partida;
        add(nivel);
        add(movimientos);
        add(empujes);
        add(deshacer);
        add(boton("Deshacer", controlador::deshacer));
        add(boton("Reiniciar", controlador::reiniciarNivel));
        actualizar();
    }

    private JButton boton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFocusable(false);
        boton.addActionListener(evento -> accion.run());
        return boton;
    }

    private void actualizar() {
        Nivel actual = partida.nivelActual();
        nivel.setText("Nivel " + partida.numeroDeNivel());
        movimientos.setText("Movimientos: " + actual.movimientos());
        empujes.setText("Empujes: " + actual.empujes());
        deshacer.setText("Deshacer usado: " + partida.usosDeDeshacer());
    }

    String textoDelNivel() {
        return nivel.getText() + " | " + movimientos.getText() + " | " + empujes.getText();
    }

    @Override
    public void jugadorSeMovio() {
        actualizar();
    }

    @Override
    public void estadoRestaurado() {
        actualizar();
    }

    @Override
    public void nivelCambiado() {
        actualizar();
    }
}
