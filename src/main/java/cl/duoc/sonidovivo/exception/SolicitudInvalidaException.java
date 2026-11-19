package cl.duoc.sonidovivo.exception;

// HTTP 400: Datos con formato correcto pero inválidos por regla de negocio.
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
