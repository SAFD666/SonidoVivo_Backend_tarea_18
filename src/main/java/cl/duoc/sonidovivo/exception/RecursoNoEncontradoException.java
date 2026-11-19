package cl.duoc.sonidovivo.exception;

// HTTP 404: El recurso solicitado no existe.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
