package sokoban.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class SonidosDelJuegoTest {

    @Test
    void cadaEventoDelModeloDisparaSuEfecto() {
        List<Efecto> reproducidos = new ArrayList<>();
        Partida partida = new Partida(List.of(new CargadorDeNivel(new FabricaDeElementos())
                .cargar(List.of("#####", "#@$.#", "#####"))));
        partida.agregarObservador(new SonidosDelJuego(reproducidos::add));

        partida.mover(Direccion.DERECHA);

        assertEquals(List.of(Efecto.EMPUJE, Efecto.PASO, Efecto.NIVEL_COMPLETADO), reproducidos);
    }

    @Test
    void todosLosEfectosTienenSuArchivoDeSonido() {
        for (Efecto efecto : Efecto.values()) {
            assertNotNull(Efecto.class.getResource(efecto.recurso()), efecto.recurso());
        }
    }
}
