package sokoban.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** Base de las pantallas completas: un título, un texto opcional y botones apilados al centro. */
abstract class PantallaCentrada extends PanelEspacial {

    private static final Dimension TAMANIO_MINIMO = new Dimension(660, 520);

    private final JLabel texto = new JLabel("", SwingConstants.CENTER);
    private final RepositorioDeImagenes imagenes;
    private final RepositorioDeFuentes fuentes;
    private final GridBagConstraints posicion = new GridBagConstraints();

    PantallaCentrada(String fondo, String titulo, float tamanioDeTitulo, RepositorioDeImagenes imagenes,
            RepositorioDeFuentes fuentes) {
        super(imagenes, fondo);
        this.imagenes = imagenes;
        this.fuentes = fuentes;
        setLayout(new GridBagLayout());
        setPreferredSize(TAMANIO_MINIMO);
        posicion.gridx = 0;
        posicion.insets = new Insets(8, 0, 8, 0);

        JLabel encabezado = new JLabel(titulo, SwingConstants.CENTER);
        encabezado.setFont(fuentes.fuente("kenvector_future", tamanioDeTitulo));
        encabezado.setForeground(new Color(150, 200, 255));
        add(encabezado, posicion);

        texto.setFont(fuentes.fuente("kenvector_future_thin", 16f));
        texto.setForeground(Color.WHITE);
        add(texto, posicion);
    }

    /** Admite varias líneas separadas por saltos de línea. */
    protected void mostrarTexto(String contenido) {
        texto.setText("<html><div align=\"center\">" + contenido.replace("\n", "<br>") + "</div></html>");
    }

    protected BotonEspacial agregarBoton(String etiqueta, Runnable accion) {
        BotonEspacial boton = new BotonEspacial(etiqueta, 240, 14f, imagenes, fuentes, accion);
        add(boton, posicion);
        return boton;
    }
}
