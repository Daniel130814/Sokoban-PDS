package sokoban.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import sokoban.model.Caja;
import sokoban.model.Nivel;
import sokoban.model.Posicion;
import sokoban.model.Tablero;
import sokoban.model.TipoCelda;
import sokoban.model.enums.Direccion;

/** Dibuja un nivel con celdas de {@link PanelDeTablero#TAMANIO_CELDA}; lo usan el tablero y las miniaturas. */
class DibujanteDeNivel {

    private static final int CELDA = PanelDeTablero.TAMANIO_CELDA;
    private static final Color CAJA_EN_DESTINO = new Color(0, 200, 0, 90);

    private final RepositorioDeImagenes imagenes;
    private final SelectorDeSprites sprites = new SelectorDeSprites();

    DibujanteDeNivel(RepositorioDeImagenes imagenes) {
        this.imagenes = imagenes;
    }

    void dibujar(Graphics2D g, Nivel nivel, Direccion orientacion) {
        Tablero tablero = nivel.tablero();
        for (int fila = 0; fila < tablero.alto(); fila++) {
            for (int columna = 0; columna < tablero.ancho(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                dibujarSprite(g, sprites.paraCelda(tablero.celdaEn(posicion)), posicion);
            }
        }
        for (Caja caja : nivel.cajas()) {
            dibujarSprite(g, sprites.paraCaja(caja), caja.posicion());
            if (tablero.celdaEn(caja.posicion()) == TipoCelda.DESTINO) {
                g.setColor(CAJA_EN_DESTINO);
                g.fillRect(caja.posicion().columna() * CELDA, caja.posicion().fila() * CELDA, CELDA, CELDA);
            }
        }
        dibujarJugador(g, nivel.jugador().posicion(), orientacion);
    }

    /** El sprite del astronauta mira hacia arriba; se rota según la dirección pedida. */
    private void dibujarJugador(Graphics2D g, Posicion posicion, Direccion orientacion) {
        double grados = switch (orientacion) {
            case ARRIBA -> 0;
            case DERECHA -> 90;
            case ABAJO -> 180;
            case IZQUIERDA -> 270;
        };
        AffineTransform original = g.getTransform();
        g.rotate(Math.toRadians(grados), (posicion.columna() + 0.5) * CELDA, (posicion.fila() + 0.5) * CELDA);
        dibujarSprite(g, sprites.paraJugador(), posicion);
        g.setTransform(original);
    }

    private void dibujarSprite(Graphics2D g, String nombre, Posicion posicion) {
        g.drawImage(imagenes.imagen(nombre), posicion.columna() * CELDA, posicion.fila() * CELDA, CELDA, CELDA, null);
    }
}
