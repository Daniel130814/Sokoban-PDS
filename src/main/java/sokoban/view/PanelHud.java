package sokoban.view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sokoban.controller.ControladorDeJuego;
import sokoban.exception.GuardadoInvalidoException;
import sokoban.logros.Logro;
import sokoban.logros.Logros;
import sokoban.model.Nivel;
import sokoban.model.ObservadorDeJuego;
import sokoban.model.Partida;

/**
 * HUD: muestra nivel, movimientos, empujes y logros, y ofrece los botones de deshacer, reiniciar,
 * guardar y cargar la partida.
 */
public class PanelHud extends JPanel implements ObservadorDeJuego {

    static final Path RUTA_DE_GUARDADO = Path.of("partida-guardada.txt");

    private final Partida partida;
    private final Logros logros;
    private final ControladorDeJuego controlador;
    private final JLabel nivel = new JLabel();
    private final JLabel movimientos = new JLabel();
    private final JLabel empujes = new JLabel();
    private final JLabel deshacer = new JLabel();
    private final JLabel logrosObtenidos = new JLabel();

    public PanelHud(Partida partida, ControladorDeJuego controlador, Logros logros) {
        super(new GridLayout(2, 1));
        this.partida = partida;
        this.controlador = controlador;
        this.logros = logros;

        JPanel datos = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        datos.add(nivel);
        datos.add(movimientos);
        datos.add(empujes);
        datos.add(deshacer);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        acciones.add(boton("Deshacer", controlador::deshacer));
        acciones.add(boton("Reiniciar", controlador::reiniciarNivel));
        acciones.add(boton("Guardar", this::guardar));
        acciones.add(boton("Cargar", this::cargar));
        acciones.add(logrosObtenidos);
        add(datos);
        add(acciones);

        logros.alDesbloquear(this::mostrarLogro);
        actualizar();
        actualizarLogros("");
    }

    private JButton boton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFocusable(false);
        boton.addActionListener(evento -> accion.run());
        return boton;
    }

    private void guardar() {
        try {
            controlador.guardar(RUTA_DE_GUARDADO);
            JOptionPane.showMessageDialog(this, "Partida guardada en " + RUTA_DE_GUARDADO.toAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la partida: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargar() {
        if (!Files.exists(RUTA_DE_GUARDADO)) {
            JOptionPane.showMessageDialog(this, "No hay una partida guardada todavía.");
            return;
        }
        try {
            controlador.cargar(RUTA_DE_GUARDADO);
        } catch (IOException | GuardadoInvalidoException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la partida: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarLogro(Logro logro) {
        actualizarLogros(" · Nuevo: " + logro.nombre());
    }

    private void actualizarLogros(String detalle) {
        logrosObtenidos.setText("Logros: " + logros.desbloqueados().size() + "/" + logros.total() + detalle);
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

    String textoDeLogros() {
        return logrosObtenidos.getText();
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
