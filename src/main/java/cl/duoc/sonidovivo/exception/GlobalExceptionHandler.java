package cl.duoc.sonidovivo.exception;

import cl.duoc.sonidovivo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Traducción de excepciones a respuestas HTTP con formato ErrorResponse.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // HTTP 400: Fallo en la validación de @Valid (campos vacíos, correo inválido, cantidades negativas).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), 400, "Bad Request",
                "Los datos enviados no son válidos", req.getRequestURI(), campos);
        return ResponseEntity.badRequest().body(body);
    }

    // HTTP 400: JSON mal formado o valor de enum inexistente (ej. rol "SUPERADMIN").
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> malFormado(Exception ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo o los parámetros de la solicitud están mal formados", req);
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponse> invalida(SolicitudInvalidaException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    // HTTP 401: Credenciales incorrectas (mensaje genérico para no revelar si el correo existe).
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> credenciales(BadCredentialsException ex, HttpServletRequest req) {
        return responder(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos", req);
    }

    // HTTP 403: Acceso denegado (usuario autenticado pero sin permisos o recurso ajeno).
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> prohibido(AccessDeniedException ex, HttpServletRequest req) {
        return responder(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción", req);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido en esta ruta", req);
    }

    // HTTP 409: Conflicto por regla de negocio (stock insuficiente, correo ya registrado, código duplicado).
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> conflicto(ConflictoException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    // HTTP 409: Violación de restricciones en la base de datos (unique, FK).
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, "La operación viola una restricción de la base de datos", req);
    }

    // HTTP 409: Conflicto de concurrencia (otro usuario modificó el mismo producto al mismo tiempo).
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> concurrencia(ObjectOptimisticLockingFailureException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, "El recurso fue modificado por otra operación; intenta nuevamente", req);
    }

    @ExceptionHandler(DemasiadosIntentosException.class)
    public ResponseEntity<ErrorResponse> intentos(DemasiadosIntentosException ex, HttpServletRequest req) {
        return responder(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), req);
    }

    // HTTP 500: Error interno no previsto (registrado en log sin datos sensibles, detalle oculto).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex, HttpServletRequest req) {
        // Errores estándar de Spring MVC (415 Content-Type, 400 parámetro faltante...) conservan su código.
        if (ex instanceof org.springframework.web.ErrorResponse springError) {
            HttpStatus status = HttpStatus.resolve(springError.getStatusCode().value());
            if (status != null && status.is4xxClientError()) {
                return responder(status, "Solicitud no válida: " + status.getReasonPhrase(), req);
            }
        }
        log.error("Error no controlado en {} {}", req.getMethod(), req.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req);
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String mensaje, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.de(status.value(), status.getReasonPhrase(), mensaje, req.getRequestURI()));
    }
}
