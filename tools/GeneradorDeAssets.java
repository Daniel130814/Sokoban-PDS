import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Genera los assets del juego (imágenes PNG y efectos WAV) dibujándolos y sintetizándolos por código.
 * Son obras originales del proyecto, dedicadas al dominio público (CC0): no hay fuentes externas.
 *
 * Uso, desde la raíz del repositorio:  java tools/GeneradorDeAssets.java
 */
public class GeneradorDeAssets {

    static final int TAM = 48;
    static final float FRECUENCIA = 22050f;

    public static void main(String[] args) throws Exception {
        Path imagenes = Path.of("src/main/resources/images");
        Path sonidos = Path.of("src/main/resources/sounds");
        Files.createDirectories(imagenes);
        Files.createDirectories(sonidos);

        guardar(imagenes, "pared", pared());
        guardar(imagenes, "piso", piso());
        guardar(imagenes, "destino", destino());
        guardar(imagenes, "hielo", hielo());
        guardar(imagenes, "cerrojo", cerrojo());
        guardar(imagenes, "muro_cerrado", muroCerrado());
        guardar(imagenes, "muro_abierto", muroAbierto());
        guardar(imagenes, "caja", caja(new Color(176, 112, 56), new Color(110, 66, 28)));
        guardar(imagenes, "caja_fragil", cajaFragil());
        guardar(imagenes, "caja_llave", cajaLlave());
        guardar(imagenes, "jugador", jugador());

        wav(sonidos, "paso", tono(260, 60, 0.35, 6));
        wav(sonidos, "empuje", tono(130, 140, 0.6, 3));
        wav(sonidos, "caja_rota", ruido(280, 0.6));
        wav(sonidos, "cerrojo", unir(tono(520, 90, 0.5, 2), tono(780, 160, 0.5, 2)));
        wav(sonidos, "deshacer", unir(tono(500, 70, 0.45, 2), tono(330, 110, 0.45, 2)));
        wav(sonidos, "nivel_completado",
                unir(tono(523, 120, 0.5, 2), tono(659, 120, 0.5, 2), tono(784, 120, 0.5, 2), tono(1047, 260, 0.5, 2)));
        System.out.println("Assets generados en src/main/resources");
    }

    // ---------- imágenes ----------

    static BufferedImage lienzo() {
        return new BufferedImage(TAM, TAM, BufferedImage.TYPE_INT_ARGB);
    }

    static Graphics2D pincel(BufferedImage imagen) {
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    static BufferedImage pared() {
        BufferedImage imagen = lienzo();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(84, 84, 96));
        g.fillRect(0, 0, TAM, TAM);
        g.setColor(new Color(52, 52, 62));
        g.setStroke(new BasicStroke(2));
        for (int y = 0; y <= TAM; y += 12) {
            g.drawLine(0, y, TAM, y);
        }
        for (int fila = 0; fila < 4; fila++) {
            int desfase = fila % 2 == 0 ? 0 : 12;
            for (int x = desfase; x < TAM; x += 24) {
                g.drawLine(x, fila * 12, x, fila * 12 + 12);
            }
        }
        g.dispose();
        return imagen;
    }

    static BufferedImage piso() {
        BufferedImage imagen = lienzo();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(226, 214, 184));
        g.fillRect(0, 0, TAM, TAM);
        g.setColor(new Color(204, 190, 156));
        g.drawRect(0, 0, TAM - 1, TAM - 1);
        g.dispose();
        return imagen;
    }

    static BufferedImage destino() {
        BufferedImage imagen = piso();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(200, 48, 48));
        g.setStroke(new BasicStroke(3));
        g.drawOval(14, 14, 20, 20);
        g.fillOval(21, 21, 6, 6);
        g.dispose();
        return imagen;
    }

    static BufferedImage hielo() {
        BufferedImage imagen = lienzo();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(170, 215, 240));
        g.fillRect(0, 0, TAM, TAM);
        g.setColor(new Color(235, 248, 255));
        g.setStroke(new BasicStroke(2));
        g.drawLine(8, 36, 24, 14);
        g.drawLine(26, 40, 40, 20);
        g.setColor(new Color(120, 175, 210));
        g.drawRect(0, 0, TAM - 1, TAM - 1);
        g.dispose();
        return imagen;
    }

    static BufferedImage cerrojo() {
        BufferedImage imagen = piso();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(212, 160, 32));
        g.fillRoundRect(12, 12, 24, 24, 6, 6);
        g.setColor(new Color(60, 40, 0));
        g.fillOval(20, 18, 8, 8);
        g.fillRect(22, 24, 4, 9);
        g.dispose();
        return imagen;
    }

    static BufferedImage muroCerrado() {
        BufferedImage imagen = lienzo();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(120, 70, 120));
        g.fillRect(0, 0, TAM, TAM);
        g.setColor(new Color(70, 36, 70));
        g.setStroke(new BasicStroke(3));
        g.drawRect(2, 2, TAM - 5, TAM - 5);
        g.drawLine(2, 2, TAM - 3, TAM - 3);
        g.drawLine(TAM - 3, 2, 2, TAM - 3);
        g.dispose();
        return imagen;
    }

    static BufferedImage muroAbierto() {
        BufferedImage imagen = piso();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(120, 70, 120));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] {6, 5}, 0));
        g.drawRect(3, 3, TAM - 7, TAM - 7);
        g.dispose();
        return imagen;
    }

    static BufferedImage caja(Color relleno, Color borde) {
        BufferedImage imagen = lienzo();
        Graphics2D g = pincel(imagen);
        g.setColor(relleno);
        g.fillRoundRect(4, 4, TAM - 8, TAM - 8, 6, 6);
        g.setColor(borde);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(4, 4, TAM - 9, TAM - 9, 6, 6);
        g.drawLine(8, 8, TAM - 9, TAM - 9);
        g.drawLine(TAM - 9, 8, 8, TAM - 9);
        g.dispose();
        return imagen;
    }

    static BufferedImage cajaFragil() {
        BufferedImage imagen = caja(new Color(214, 170, 120), new Color(140, 96, 56));
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(60, 36, 12));
        g.setStroke(new BasicStroke(2));
        g.drawLine(24, 4, 20, 16);
        g.drawLine(20, 16, 27, 26);
        g.drawLine(27, 26, 22, 44);
        g.dispose();
        return imagen;
    }

    static BufferedImage cajaLlave() {
        BufferedImage imagen = caja(new Color(220, 180, 50), new Color(130, 96, 10));
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(60, 40, 0));
        g.setStroke(new BasicStroke(3));
        g.drawOval(14, 14, 12, 12);
        g.drawLine(26, 20, 36, 20);
        g.drawLine(32, 20, 32, 26);
        g.drawLine(36, 20, 36, 26);
        g.dispose();
        return imagen;
    }

    static BufferedImage jugador() {
        BufferedImage imagen = lienzo();
        Graphics2D g = pincel(imagen);
        g.setColor(new Color(40, 90, 200));
        g.fillOval(7, 7, TAM - 14, TAM - 14);
        g.setColor(new Color(20, 50, 130));
        g.setStroke(new BasicStroke(2));
        g.drawOval(7, 7, TAM - 15, TAM - 15);
        g.setColor(Color.WHITE);
        g.fillOval(15, 17, 7, 9);
        g.fillOval(27, 17, 7, 9);
        g.setColor(Color.BLACK);
        g.fillOval(18, 21, 3, 4);
        g.fillOval(30, 21, 3, 4);
        g.dispose();
        return imagen;
    }

    static void guardar(Path carpeta, String nombre, BufferedImage imagen) throws IOException {
        ImageIO.write(imagen, "png", carpeta.resolve(nombre + ".png").toFile());
    }

    // ---------- sonidos ----------

    static short[] tono(double hz, int milisegundos, double volumen, double decaimiento) {
        int muestras = (int) (FRECUENCIA * milisegundos / 1000);
        short[] datos = new short[muestras];
        for (int i = 0; i < muestras; i++) {
            double t = i / FRECUENCIA;
            double envolvente = Math.exp(-decaimiento * i / muestras);
            datos[i] = (short) (Math.sin(2 * Math.PI * hz * t) * envolvente * volumen * Short.MAX_VALUE);
        }
        return datos;
    }

    static short[] ruido(int milisegundos, double volumen) {
        Random azar = new Random(42);
        int muestras = (int) (FRECUENCIA * milisegundos / 1000);
        short[] datos = new short[muestras];
        for (int i = 0; i < muestras; i++) {
            double envolvente = Math.exp(-5.0 * i / muestras);
            datos[i] = (short) ((azar.nextDouble() * 2 - 1) * envolvente * volumen * Short.MAX_VALUE);
        }
        return datos;
    }

    static short[] unir(short[]... partes) {
        int total = 0;
        for (short[] parte : partes) {
            total += parte.length;
        }
        short[] resultado = new short[total];
        int posicion = 0;
        for (short[] parte : partes) {
            System.arraycopy(parte, 0, resultado, posicion, parte.length);
            posicion += parte.length;
        }
        return resultado;
    }

    static void wav(Path carpeta, String nombre, short[] muestras) throws IOException {
        byte[] bytes = new byte[muestras.length * 2];
        for (int i = 0; i < muestras.length; i++) {
            bytes[2 * i] = (byte) (muestras[i] & 0xFF);
            bytes[2 * i + 1] = (byte) ((muestras[i] >> 8) & 0xFF);
        }
        AudioFormat formato = new AudioFormat(FRECUENCIA, 16, 1, true, false);
        try (AudioInputStream audio = new AudioInputStream(
                new ByteArrayInputStream(bytes), formato, muestras.length)) {
            AudioSystem.write(audio, AudioFileFormat.Type.WAVE, new File(carpeta.toFile(), nombre + ".wav"));
        }
    }
}
