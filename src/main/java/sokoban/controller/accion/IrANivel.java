package sokoban.controller.accion;

import sokoban.model.Partida;

/** Empieza el nivel elegido (numerado desde 1) con todo como al principio; sirve para el selector de niveles. */
public record IrANivel(int numero) implements Accion {

    @Override
    public boolean ejecutar(Partida partida) {
        if (numero < 1 || numero > partida.cantidadDeNiveles()) {
            return false;
        }
        partida.irANivel(numero);
        return true;
    }

    @Override
    public String comoTexto() {
        return "IR_A_NIVEL " + numero;
    }
}
