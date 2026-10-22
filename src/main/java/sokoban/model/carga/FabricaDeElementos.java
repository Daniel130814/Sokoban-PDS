package sokoban.model.carga;

import java.util.Optional;
import java.util.OptionalInt;
import sokoban.exception.NivelInvalidoException;
import sokoban.model.Caja;
import sokoban.model.CajaFragil;
import sokoban.model.CajaLlave;
import sokoban.model.CajaNormal;
import sokoban.model.Posicion;
import sokoban.model.TipoCelda;

/**
 * Simple Factory: único lugar que conoce la leyenda de caracteres de los archivos de nivel.
 * Leyenda: '#' pared, ' ' piso, '.' destino, '$' caja, '*' caja sobre destino, '%' caja frágil,
 * '@' jugador, '+' jugador sobre destino, '~' hielo (terreno resbaladizo).
 * Llaves: 'a'-'i' caja llave de id 1-9, '1'-'9' cerrojo de ese id, 'A'-'I' muro cerrado que abre
 * el cerrojo de id 1-9.
 */
public class FabricaDeElementos {

    public TipoCelda crearCelda(char simbolo) {
        if (idDeCerrojo(simbolo).isPresent()) {
            return TipoCelda.CERROJO;
        }
        if (idDeMuro(simbolo).isPresent()) {
            return TipoCelda.MURO_CERRADO;
        }
        if (idDeLlave(simbolo).isPresent()) {
            return TipoCelda.PISO;
        }
        return switch (simbolo) {
            case '#' -> TipoCelda.PARED;
            case ' ', '$', '%', '@' -> TipoCelda.PISO;
            case '.', '*', '+' -> TipoCelda.DESTINO;
            case '~' -> TipoCelda.HIELO;
            default -> throw new NivelInvalidoException("Carácter desconocido en el nivel: '" + simbolo + "'");
        };
    }

    public Optional<Caja> crearCaja(char simbolo, Posicion posicion) {
        OptionalInt llave = idDeLlave(simbolo);
        if (llave.isPresent()) {
            return Optional.of(new CajaLlave(posicion, llave.getAsInt()));
        }
        return switch (simbolo) {
            case '$', '*' -> Optional.of(new CajaNormal(posicion));
            case '%' -> Optional.of(new CajaFragil(posicion));
            default -> Optional.empty();
        };
    }

    public boolean esJugador(char simbolo) {
        return simbolo == '@' || simbolo == '+';
    }

    public OptionalInt idDeCerrojo(char simbolo) {
        return simbolo >= '1' && simbolo <= '9' ? OptionalInt.of(simbolo - '0') : OptionalInt.empty();
    }

    public OptionalInt idDeMuro(char simbolo) {
        return simbolo >= 'A' && simbolo <= 'I' ? OptionalInt.of(simbolo - 'A' + 1) : OptionalInt.empty();
    }

    private OptionalInt idDeLlave(char simbolo) {
        return simbolo >= 'a' && simbolo <= 'i' ? OptionalInt.of(simbolo - 'a' + 1) : OptionalInt.empty();
    }
}
