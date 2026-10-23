package sokoban.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import sokoban.model.enums.Direccion;

public class Nivel implements ObservadorDeCerrojo {

    private final Tablero tablero;
    private final Jugador jugador;
    private final List<Caja> cajas;
    private final Map<Posicion, Cerrojo> cerrojos = new HashMap<>();
    private final DifusorDeEventos observadores = new DifusorDeEventos();
    private int movimientos;
    private int empujes;

    public Nivel(Tablero tablero, Jugador jugador, List<Caja> cajas) {
        this(tablero, jugador, cajas, List.of());
    }

    public Nivel(Tablero tablero, Jugador jugador, List<Caja> cajas, Collection<Cerrojo> cerrojos) {
        this.tablero = tablero;
        this.jugador = jugador;
        this.cajas = new ArrayList<>(cajas);
        cerrojos.forEach(cerrojo -> {
            this.cerrojos.put(cerrojo.posicion(), cerrojo);
            cerrojo.agregarObservador(this);
        });
    }

    public void agregarObservador(ObservadorDeJuego observador) {
        observadores.agregar(observador);
    }

    @Override
    public void alActivarseCerrojo(Cerrojo cerrojo) {
        observadores.cerrojoActivado();
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

    public int movimientos() {
        return movimientos;
    }

    public int empujes() {
        return empujes;
    }

    /** Originator del Memento: captura todo lo que cambia durante el juego. */
    public EstadoNivel guardarEstado() {
        Map<Cerrojo, Boolean> activos = new HashMap<>();
        cerrojos.values().forEach(cerrojo -> activos.put(cerrojo, cerrojo.estaActivo()));
        return new EstadoNivel(jugador.posicion(), cajas,
                cajas.stream().map(Caja::guardarEstado).toList(),
                tablero.copiarCeldas(), activos, movimientos, empujes);
    }

    public void restaurar(EstadoNivel estado) {
        jugador.irA(estado.jugador());
        cajas.clear();
        cajas.addAll(estado.cajas());
        estado.estadosDeCajas().forEach(EstadoCaja::restaurar);
        tablero.restaurarCeldas(estado.celdas());
        estado.cerrojosActivos().forEach(Cerrojo::restaurarActivo);
        movimientos = estado.movimientos();
        empujes = estado.empujes();
        observadores.estadoRestaurado();
    }

    public boolean moverJugador(Direccion direccion) {
        Posicion destino = jugador.posicion().desplazar(direccion);
        if (!tablero.esTransitable(destino)) {
            return false;
        }
        Optional<Caja> enDestino = cajaEn(destino);
        int cajasAntes = cajas.size();
        if (enDestino.isPresent() && !empujar(enDestino.get(), direccion)) {
            return false;
        }
        jugador.irA(destino);
        movimientos++;
        if (enDestino.isPresent()) {
            empujes++;
            observadores.cajaEmpujada();
            if (cajas.size() < cajasAntes) {
                observadores.cajaSeRompio();
            }
        }
        observadores.jugadorSeMovio();
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
