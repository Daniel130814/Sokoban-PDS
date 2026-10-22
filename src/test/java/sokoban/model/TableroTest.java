package sokoban.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sokoban.model.enums.TipoCelda.PARED;
import static sokoban.model.enums.TipoCelda.PISO;

import org.junit.jupiter.api.Test;
import sokoban.model.enums.TipoCelda;

class TableroTest {

    @Test
    void rechazaFilasDeDistintoLargo() {
        TipoCelda[][] irregular = {{PISO, PISO}, {PISO}};

        assertThrows(IllegalArgumentException.class, () -> new Tablero(irregular));
    }

    @Test
    void noCambiaSiSeModificaElArregloOriginal() {
        TipoCelda[][] celdas = {{PISO, PISO}};
        Tablero copia = new Tablero(celdas);

        celdas[0][1] = PARED;

        assertTrue(copia.esTransitable(new Posicion(0, 1)));
    }

    @Test
    void lasPosicionesFueraDelTableroNoSonTransitables() {
        Tablero tablero = new Tablero(new TipoCelda[][] {{PISO}});

        assertFalse(tablero.esTransitable(new Posicion(0, 1)));
        assertEquals(1, tablero.ancho());
    }
}
