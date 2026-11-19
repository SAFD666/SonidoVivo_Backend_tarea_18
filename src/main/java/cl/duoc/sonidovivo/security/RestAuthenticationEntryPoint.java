package cl.duoc.sonidovivo.security;

import cl.duoc.sonidovivo.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 401: ruta protegida sin token, con token alterado o vencido.
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        Object detalle = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_ERROR);
        String mensaje = detalle != null ? detalle.toString() : "Debes iniciar sesión para acceder a este recurso";
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("WWW-Authenticate", "Bearer");
        objectMapper.writeValue(response.getWriter(),
                ErrorResponse.de(401, "Unauthorized", mensaje, request.getRequestURI()));
    }
}
