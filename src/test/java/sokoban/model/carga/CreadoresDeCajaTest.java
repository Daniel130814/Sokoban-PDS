package sokoban.model.carga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import sokoban.model.Caja;
import sokoban.model.CajaFragil;
import sokoban.model.CajaLlave;
import sokoban.model.CajaNormal;
import sokoban.model.Posicion;

class CreadoresDeCajaTest {

    private final Posicion posicion = new Posicion(2, 3);

    @Test
    void cadaSimboloSeResuelveConSuCreador() {
        FabricaDeElementos fabrica = new FabricaDeElementos();

        assertInstanceOf(CajaNormal.class, fabrica.crearCaja('$', posicion).orElseThrow());
        assertInstanceOf(CajaNormal.class, fabrica.crearCaja('*', posicion).orElseThrow());
        assertInstanceOf(CajaFragil.class, fabrica.crearCaja('%', posicion).orElseThrow());
        assertInstanceOf(CajaLlave.class, fabrica.crearCaja('a', posicion).orElseThrow());
        assertInstanceOf(CajaLlave.class, fabrica.crearCaja('i', posicion).orElseThrow());
    }

    @Test
    void losSimbolosQueNoSonCajasNoCreanNada() {
        FabricaDeElementos fabrica = new FabricaDeElementos();

        assertTrue(fabrica.crearCaja('#', posicion).isEmpty());
        assertTrue(fabrica.crearCaja(' ', posicion).isEmpty());
        assertTrue(fabrica.crearCaja('A', posicion).isEmpty());
        assertTrue(fabrica.crearCaja('5', posicion).isEmpty());
    }

    @Test
    void elCreadorDeLlaveTomaElIdDeLaLetra() {
        CreadorDeCaja creador = new CreadorDeCajaLlave();

        assertEquals(1, ((CajaLlave) creador.crearCaja('a', posicion)).id());
        assertEquals(9, ((CajaLlave) creador.crearCaja('i', posicion)).id());
        assertThrows(IllegalArgumentException.class, () -> creador.crearCaja('j', posicion));
    }

    @Test
    void sePuedeSumarUnTipoDeCajaRegistrandoUnCreadorSinTocarLaFabrica() {
        Caja especial = new CajaNormal(posicion);
        CreadorDeCaja creadorNuevo = (simbolo, donde) -> especial;
        FabricaDeElementos fabrica = new FabricaDeElementos(Map.of('x', creadorNuevo));

        Optional<Caja> creada = fabrica.crearCaja('x', posicion);

        assertSame(especial, creada.orElseThrow());
        assertTrue(fabrica.crearCaja('$', posicion).isEmpty());
    }
}
