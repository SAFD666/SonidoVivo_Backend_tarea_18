package cl.duoc.sonidovivo.exception;

// HTTP 409: Conflicto con el estado de los datos o regla de negocio.
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
