package sokoban.view;

import java.util.EnumMap;
import java.util.Map;
import sokoban.model.Caja;
import sokoban.model.CajaFragil;
import sokoban.model.CajaLlave;
import sokoban.model.TipoCelda;

/** Decide qué imagen corresponde a cada elemento del modelo; el modelo no sabe nada de imágenes. */
public class SelectorDeSprites {

    private final Map<TipoCelda, String> celdas = new EnumMap<>(TipoCelda.class);

    public SelectorDeSprites() {
        celdas.put(TipoCelda.PARED, "pared");
        celdas.put(TipoCelda.PISO, "piso");
        celdas.put(TipoCelda.DESTINO, "destino");
        celdas.put(TipoCelda.HIELO, "hielo");
        celdas.put(TipoCelda.CERROJO, "cerrojo");
        celdas.put(TipoCelda.MURO_CERRADO, "muro_cerrado");
        celdas.put(TipoCelda.MURO_ABIERTO, "muro_abierto");
    }

    public String paraCelda(TipoCelda celda) {
        return celdas.get(celda);
    }

    public String paraCaja(Caja caja) {
        if (caja instanceof CajaFragil) {
            return "caja_fragil";
        }
        if (caja instanceof CajaLlave) {
            return "caja_llave";
        }
        return "caja";
    }

    public String paraJugador() {
        return "jugador";
    }
}
