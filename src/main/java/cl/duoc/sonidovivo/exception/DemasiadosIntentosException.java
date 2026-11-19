package cl.duoc.sonidovivo.exception;

// HTTP 429: Demasiados intentos de inicio de sesión (rate limit).
public class DemasiadosIntentosException extends RuntimeException {
    public DemasiadosIntentosException(String mensaje) {
        super(mensaje);
    }
}
