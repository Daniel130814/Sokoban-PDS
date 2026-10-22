package sokoban.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import sokoban.model.enums.Direccion;

public class Nivel {

    private final Tablero tablero;
    private final Jugador jugador;
    private final List<Caja> cajas;

    public Nivel(Tablero tablero, Jugador jugador, List<Caja> cajas) {
        this.tablero = tablero;
        this.jugador = jugador;
        this.cajas = List.copyOf(cajas);
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
        Posicion siguiente = caja.posicion().desplazar(direccion);
        if (!tablero.esTransitable(siguiente) || cajaEn(siguiente).isPresent()) {
            return false;
        }
        caja.irA(siguiente);
        caja.alSerEmpujada();
        return true;
    }
}
