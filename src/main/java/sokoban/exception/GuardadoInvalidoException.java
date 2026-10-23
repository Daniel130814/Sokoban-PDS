package sokoban.exception;

public class GuardadoInvalidoException extends RuntimeException {

    public GuardadoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public GuardadoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
