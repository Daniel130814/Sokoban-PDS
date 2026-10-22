package sokoban.exception;

public class NivelInvalidoException extends RuntimeException {

    public NivelInvalidoException(String mensaje) {
        super(mensaje);
    }

    public NivelInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
