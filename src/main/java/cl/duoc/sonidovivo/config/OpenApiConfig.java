package cl.duoc.sonidovivo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI: http://IP_PUBLICA_EC2:8080/swagger-ui.html
 * El botón "Authorize" permite pegar el accessToken del login para probar rutas protegidas.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Sonido Vivo API",
        version = "v1",
        description = "API REST de la tienda de instrumentos Sonido Vivo: autenticación JWT, usuarios y roles."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
