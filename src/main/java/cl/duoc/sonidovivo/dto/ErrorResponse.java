package cl.duoc.sonidovivo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

// Formato de error estándar para toda la API.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        Map<String, String> campos
) {
    public static ErrorResponse de(int status, String error, String mensaje, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, mensaje, path, null);
    }
}
