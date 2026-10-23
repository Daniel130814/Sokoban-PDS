package sokoban.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/** Reproduce los efectos con javax.sound.sampled. Si no hay audio disponible, el juego sigue en silencio. */
public class ReproductorJavaSound implements ReproductorDeSonido {

    private static final System.Logger REGISTRO = System.getLogger(ReproductorJavaSound.class.getName());

    private final Map<Efecto, Clip> clips = new EnumMap<>(Efecto.class);

    @Override
    public void reproducir(Efecto efecto) {
        try {
            Clip clip = clips.computeIfAbsent(efecto, this::cargar);
            if (clip == null) {
                return;
            }
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (RuntimeException e) {
            REGISTRO.log(System.Logger.Level.WARNING, "No se pudo reproducir " + efecto, e);
        }
    }

    private Clip cargar(Efecto efecto) {
        try (InputStream entrada = ReproductorJavaSound.class.getResourceAsStream(efecto.recurso())) {
            if (entrada == null) {
                return null;
            }
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(entrada))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                return clip;
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException
                | IllegalArgumentException e) {
            REGISTRO.log(System.Logger.Level.WARNING, "No se pudo cargar " + efecto.recurso(), e);
            return null;
        }
    }
}
