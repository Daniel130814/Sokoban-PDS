package sokoban.model.carga;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import sokoban.exception.NivelInvalidoException;
import sokoban.model.Caja;
import sokoban.model.Posicion;
import sokoban.model.TipoCelda;

/**
 * Único lugar que conoce la leyenda de caracteres de los archivos de nivel. Las celdas se resuelven
 * acá; las cajas se delegan en un {@link CreadorDeCaja} por símbolo (Factory Method).
 * Leyenda: '#' pared, ' ' piso, '.' destino, '$' caja, '*' caja sobre destino, '%' caja frágil,
 * '@' jugador, '+' jugador sobre destino, '~' hielo (terreno resbaladizo).
 * Llaves: 'a'-'i' caja llave de id 1-9, '1'-'9' cerrojo de ese id, 'A'-'I' muro cerrado que abre
 * el cerrojo de id 1-9.
 */
public class FabricaDeElementos {

    private final Map<Character, CreadorDeCaja> creadoresDeCaja;

    public FabricaDeElementos() {
        this(creadoresEstandar());
    }

    /** Permite registrar otros creadores de caja sin tocar esta clase (por ejemplo, en pruebas). */
    public FabricaDeElementos(Map<Character, CreadorDeCaja> creadoresDeCaja) {
        this.creadoresDeCaja = Map.copyOf(creadoresDeCaja);
    }

    private static Map<Character, CreadorDeCaja> creadoresEstandar() {
        Map<Character, CreadorDeCaja> creadores = new HashMap<>();
        CreadorDeCaja normal = new CreadorDeCajaNormal();
        creadores.put('$', normal);
        creadores.put('*', normal);
        creadores.put('%', new CreadorDeCajaFragil());
        CreadorDeCaja llave = new CreadorDeCajaLlave();
        for (char letra = 'a'; letra <= 'i'; letra++) {
            creadores.put(letra, llave);
        }
        return creadores;
    }

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
        return Optional.ofNullable(creadoresDeCaja.get(simbolo))
                .map(creador -> creador.crearCaja(simbolo, posicion));
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
        return CreadorDeCajaLlave.idDe(simbolo);
    }
}
