package sokoban.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import sokoban.model.Caja;
import sokoban.model.CajaFragil;
import sokoban.model.Nivel;
import sokoban.model.Posicion;
import sokoban.model.Tablero;
import sokoban.model.TipoCelda;
import sokoban.model.enums.Direccion;

/** Dibuja un nivel con celdas de {@link PanelDeTablero#TAMANIO_CELDA}; lo usan el tablero y las miniaturas. */
class DibujanteDeNivel {

    private static final int CELDA = PanelDeTablero.TAMANIO_CELDA;
    private static final int LADO_DE_CONTADOR = 20;
    private static final Color CAJA_EN_DESTINO = new Color(0, 200, 0, 90);

    private final RecursosVisuales recursos;
    private final SelectorDeSprites sprites = new SelectorDeSprites();

    DibujanteDeNivel(RecursosVisuales recursos) {
        this.recursos = recursos;
    }

    /** Dibuja el nivel sin contadores: es lo que usan las miniaturas del catálogo. */
    void dibujar(Graphics2D g, Nivel nivel, Direccion orientacion) {
        dibujar(g, nivel, orientacion, false);
    }

    /** Con {@code contadores}, cada caja frágil muestra arriba cuántos empujes más aguanta. */
    void dibujar(Graphics2D g, Nivel nivel, Direccion orientacion, boolean contadores) {
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
        if (contadores) {
            nivel.cajas().stream().filter(CajaFragil.class::isInstance).map(CajaFragil.class::cast)
                    .forEach(caja -> dibujarContador(g, caja));
        }
        dibujarJugador(g, nivel.jugador().posicion(), orientacion);
    }

    /** Insignia chica sobre la caja con su resistencia: verde con 3, amarilla con 2 y roja con 1. */
    private void dibujarContador(Graphics2D g, CajaFragil caja) {
        int resistencia = caja.resistencia();
        int x = caja.posicion().columna() * CELDA + CELDA / 2 - LADO_DE_CONTADOR / 2;
        int y = caja.posicion().fila() * CELDA + 1;
        Color color = resistencia >= 3 ? new Color(60, 200, 90) : resistencia == 2 ? new Color(240, 200, 60)
                : new Color(235, 70, 70);
        g.setColor(new Color(8, 10, 28, 215));
        g.fillRoundRect(x, y, LADO_DE_CONTADOR, LADO_DE_CONTADOR, 10, 10);
        g.setColor(color);
        g.setStroke(new java.awt.BasicStroke(2f));
        g.drawRoundRect(x, y, LADO_DE_CONTADOR, LADO_DE_CONTADOR, 10, 10);
        g.setStroke(new java.awt.BasicStroke(1f));
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14)); // la fuente del tema deforma los dígitos
        FontMetrics metricas = g.getFontMetrics();
        String texto = String.valueOf(resistencia);
        g.drawString(texto, x + (LADO_DE_CONTADOR - metricas.stringWidth(texto)) / 2,
                y + (LADO_DE_CONTADOR + metricas.getAscent() - metricas.getDescent()) / 2);
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
        g.drawImage(recursos.imagen(nombre), posicion.columna() * CELDA, posicion.fila() * CELDA, CELDA, CELDA, null);
    }
}
