package sokoban.model.carga;

import java.util.Optional;
import sokoban.exception.NivelInvalidoException;
import sokoban.model.Caja;
import sokoban.model.CajaNormal;
import sokoban.model.Posicion;
import sokoban.model.enums.TipoCelda;

/**
 * Simple Factory: único lugar que conoce la leyenda de caracteres de los archivos de nivel.
 * Leyenda: '#' pared, ' ' piso, '.' destino, '$' caja, '*' caja sobre destino,
 * '@' jugador, '+' jugador sobre destino.
 */
public class FabricaDeElementos {

    public TipoCelda crearCelda(char simbolo) {
        return switch (simbolo) {
            case '#' -> TipoCelda.PARED;
            case ' ', '$', '@' -> TipoCelda.PISO;
            case '.', '*', '+' -> TipoCelda.DESTINO;
            default -> throw new NivelInvalidoException("Carácter desconocido en el nivel: '" + simbolo + "'");
        };
    }

    public Optional<Caja> crearCaja(char simbolo, Posicion posicion) {
        return switch (simbolo) {
            case '$', '*' -> Optional.of(new CajaNormal(posicion));
            default -> Optional.empty();
        };
    }

    public boolean esJugador(char simbolo) {
        return simbolo == '@' || simbolo == '+';
    }
}
