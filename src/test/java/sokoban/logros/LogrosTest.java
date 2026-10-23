package sokoban.logros;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.CajaFragil;
import sokoban.model.Partida;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;
import sokoban.model.enums.Direccion;

class LogrosTest {

    private static final CargadorDeNivel CARGADOR = new CargadorDeNivel(new FabricaDeElementos());

    private static Partida partida(String... filas) {
        return new Partida(List.of(CARGADOR.cargar(List.of(filas))));
    }

    private static Logros observar(Partida partida) {
        Logros logros = new Logros(partida, CatalogoDeLogros.estandar());
        partida.agregarObservador(logros);
        return logros;
    }

    private static List<String> nombres(Logros logros) {
        return logros.desbloqueados().stream().map(Logro::nombre).toList();
    }

    @Test
    void alPrincipioNoHayLogrosDesbloqueados() {
        Logros logros = observar(partida("#####", "#@$.#", "#####"));

        assertTrue(logros.desbloqueados().isEmpty());
        assertEquals(5, logros.total());
    }

    @Test
    void completarUnNivelSinDeshacerDesbloqueaPrimerPasoYSinRed() {
        Partida partida = partida("#####", "#@$.#", "#####");
        Logros logros = observar(partida);

        partida.mover(Direccion.DERECHA);

        assertEquals(List.of("Primer paso", "Sin red"), nombres(logros));
    }

    @Test
    void completarConDeshacerNoDaSinRed() {
        Partida partida = partida("#######", "#@ $ .#", "#######");
        Logros logros = observar(partida);
        partida.mover(Direccion.DERECHA);
        partida.deshacer();

        partida.mover(Direccion.DERECHA);
        partida.mover(Direccion.DERECHA);
        partida.mover(Direccion.DERECHA);

        assertEquals(List.of("Primer paso"), nombres(logros));
    }

    @Test
    void romperUnaCajaDesbloqueaRompecajas() {
        Partida partida = partida("########", "#@%   .#", "########");
        Logros logros = observar(partida);

        for (int i = 0; i < CajaFragil.RESISTENCIA_INICIAL; i++) {
            partida.mover(Direccion.DERECHA);
        }

        assertTrue(nombres(logros).contains("Rompecajas"));
    }

    @Test
    void activarUnCerrojoDesbloqueaCerrajero() {
        Partida partida = partida("#######", "#@a1A.#", "#######");
        Logros logros = observar(partida);

        partida.mover(Direccion.DERECHA);

        assertTrue(nombres(logros).contains("Cerrajero"));
    }

    @Test
    void cadaLogroSeDesbloqueaUnaSolaVezYAvisaAlOyente() {
        Partida partida = partida("#########", "#@$     .#", "#########");
        Logro primerEmpuje = new Logro("Primer empuje", "Empujá una caja", e -> e.empujes() >= 1);
        Logros logros = new Logros(partida, List.of(primerEmpuje));
        partida.agregarObservador(logros);
        List<String> avisos = new ArrayList<>();
        logros.alDesbloquear(logro -> avisos.add(logro.nombre()));

        for (int i = 0; i < 3; i++) {
            partida.mover(Direccion.DERECHA);
        }

        assertEquals(3, logros.estadisticas().empujes());
        assertEquals(List.of("Primer empuje"), avisos);
    }

    @Test
    void sePuedeUsarUnCatalogoPropioConCriteriosNuevos() {
        Partida partida = partida("#####", "#@$.#", "#####");
        Logro primerEmpuje = new Logro("Primer empuje", "Empujá una caja", e -> e.empujes() >= 1);
        Logros logros = new Logros(partida, List.of(primerEmpuje));
        partida.agregarObservador(logros);

        assertFalse(logros.desbloqueados().contains(primerEmpuje));
        partida.mover(Direccion.DERECHA);

        assertEquals(List.of(primerEmpuje), logros.desbloqueados());
    }
}
