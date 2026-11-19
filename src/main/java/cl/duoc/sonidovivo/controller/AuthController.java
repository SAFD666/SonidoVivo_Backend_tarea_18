package cl.duoc.sonidovivo.controller;

import cl.duoc.sonidovivo.dto.*;
import cl.duoc.sonidovivo.service.AuthService;
import cl.duoc.sonidovivo.service.UsuarioService;
import cl.duoc.sonidovivo.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "1. Autenticación", description = "Registro, inicio de sesión con JWT y datos del usuario autenticado")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar cliente", description = "Crea una cuenta con rol CLIENTE (lo asigna el servidor). Pantalla: Crear cuenta.")
    @ApiResponse(responseCode = "201", description = "Cuenta creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest req) {
        return authService.registrar(req);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Comprueba correo y contraseña (hash BCrypt) y entrega un JWT con vigencia. Pantalla: Login.")
    @ApiResponse(responseCode = "200", description = "Token emitido")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos (mensaje genérico)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "429", description = "Demasiados intentos fallidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mi perfil", description = "Devuelve el usuario dueño del token. El id sale del token, no de la URL.")
    @ApiResponse(responseCode = "200", description = "Datos del usuario autenticado")
    @ApiResponse(responseCode = "401", description = "Sin token, token alterado o vencido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public UsuarioResponse me(@AuthenticationPrincipal UsuarioAutenticado yo) {
        return usuarioService.obtener(yo.id(), yo);
    }
}
