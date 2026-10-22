package sokoban.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import sokoban.model.enums.Direccion;

public class Nivel {

    private final Tablero tablero;
    private final Jugador jugador;
    private final List<Caja> cajas;
    private final Map<Posicion, Cerrojo> cerrojos = new HashMap<>();

    public Nivel(Tablero tablero, Jugador jugador, List<Caja> cajas) {
        this(tablero, jugador, cajas, List.of());
    }

    public Nivel(Tablero tablero, Jugador jugador, List<Caja> cajas, Collection<Cerrojo> cerrojos) {
        this.tablero = tablero;
        this.jugador = jugador;
        this.cajas = new ArrayList<>(cajas);
        cerrojos.forEach(cerrojo -> this.cerrojos.put(cerrojo.posicion(), cerrojo));
    }

    public Tablero tablero() {
        return tablero;
    }

    public Jugador jugador() {
        return jugador;
    }

    public List<Caja> cajas() {
        return Collections.unmodifiableList(cajas);
    }

    public Optional<Caja> cajaEn(Posicion posicion) {
        return cajas.stream().filter(caja -> caja.posicion().equals(posicion)).findFirst();
    }

    public boolean estaResuelto() {
        long cajasEnDestino = cajas.stream()
                .filter(caja -> tablero.celdaEn(caja.posicion()) == TipoCelda.DESTINO)
                .count();
        return cajasEnDestino == tablero.cantidadDeDestinos();
    }

    /** Si se rompió una caja y quedan menos cajas que destinos, el nivel ya no puede resolverse. */
    public boolean quedoIrresoluble() {
        return cajas.size() < tablero.cantidadDeDestinos();
    }

    public boolean moverJugador(Direccion direccion) {
        Posicion destino = jugador.posicion().desplazar(direccion);
        if (!tablero.esTransitable(destino)) {
            return false;
        }
        Optional<Caja> enDestino = cajaEn(destino);
        if (enDestino.isPresent() && !empujar(enDestino.get(), direccion)) {
            return false;
        }
        jugador.irA(destino);
        return true;
    }

    private boolean empujar(Caja caja, Direccion direccion) {
        Posicion llegada = caja.posicion().desplazar(direccion);
        if (!tablero.esTransitable(llegada) || cajaEn(llegada).isPresent()) {
            return false;
        }
        Posicion destino = tablero.reglaEn(llegada)
                .posicionFinal(llegada, direccion, tablero, posicion -> cajaEn(posicion).isPresent());
        caja.irA(destino);
        caja.alSerEmpujada();
        Cerrojo cerrojo = cerrojos.get(destino);
        if (cerrojo != null) {
            caja.alAsentarseEn(cerrojo);
        }
        if (caja.estaRota()) {
            cajas.remove(caja);
        }
        return true;
    }
}
