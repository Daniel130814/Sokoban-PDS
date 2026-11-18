package sokoban.model.campania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;
import sokoban.model.Nivel;
import sokoban.model.carga.CargadorDeNivel;
import sokoban.model.carga.FabricaDeElementos;

class CampaniaTest {

    private final CargadorDeNivel cargador = new CargadorDeNivel(new FabricaDeElementos());

    private NivelDeCampania hoja(String titulo) {
        return new NivelDeCampania(titulo, "detalle", cargador.cargar(List.of("#####", "#@$.#", "#####")));
    }

    @Test
    void unNivelSueltoSeTrataComoUnElementoConUnSoloNivel() {
        NivelDeCampania hoja = hoja("Uno");

        assertEquals(1, hoja.cantidadDeNiveles());
        assertSame(hoja.nivel(), hoja.niveles().get(0));
    }

    @Test
    void unMundoDevuelveLosNivelesDeSusHijosEnOrden() {
        NivelDeCampania a = hoja("A");
        NivelDeCampania b = hoja("B");
        Mundo mundo = new Mundo("Mundo", "d").agregar(a).agregar(b);

        assertEquals(List.of(a.nivel(), b.nivel()), mundo.niveles());
        assertEquals(2, mundo.cantidadDeNiveles());
    }

    @Test
    void losMundosSePuedenAnidarYSeAplanan() {
        NivelDeCampania a = hoja("A");
        NivelDeCampania b = hoja("B");
        NivelDeCampania c = hoja("C");
        Mundo campania = new Mundo("Campaña", "d")
                .agregar(new Mundo("1", "d").agregar(a).agregar(b))
                .agregar(new Mundo("2", "d").agregar(c));

        List<Nivel> niveles = campania.niveles();

        assertEquals(List.of(a.nivel(), b.nivel(), c.nivel()), niveles);
        assertEquals(3, campania.cantidadDeNiveles());
    }

    @Test
    void laCampaniaEstandarTieneTutorialYDesafiosConNueveNiveles() {
        Mundo campania = CampaniaEstandar.crear(cargador::cargarDesdeRecurso);

        assertEquals(9, campania.cantidadDeNiveles());
        assertEquals(List.of("Tutorial", "Desafíos"),
                campania.hijos().stream().map(ElementoDeCampania::titulo).toList());
        assertEquals(4, campania.hijos().get(0).cantidadDeNiveles());
    }
}
