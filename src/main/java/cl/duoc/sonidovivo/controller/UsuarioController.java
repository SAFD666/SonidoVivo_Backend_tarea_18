package cl.duoc.sonidovivo.controller;

import cl.duoc.sonidovivo.dto.*;
import cl.duoc.sonidovivo.security.UsuarioAutenticado;
import cl.duoc.sonidovivo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "2. Usuarios", description = "CRUD de usuarios y roles. 401 sin token / 403 sin permiso")
@ApiResponse(responseCode = "401", description = "Sin token, token alterado o vencido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "403", description = "Rol insuficiente o cuenta de otra persona", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar usuarios", description = "Solo Administrador.")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver usuario", description = "Administrador ve cualquiera; los demás solo su propia cuenta.")
    @ApiResponse(responseCode = "404", description = "No existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public UsuarioResponse obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado yo) {
        return usuarioService.obtener(id, yo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear usuario con rol", description = "Solo Administrador. Permite crear vendedores u otros administradores.")
    @ApiResponse(responseCode = "409", description = "Correo duplicado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public UsuarioResponse crear(@Valid @RequestBody UsuarioCrearRequest req) {
        return usuarioService.crear(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar usuario", description = "Cada uno modifica su cuenta; Administrador puede modificar cualquiera. No cambia rol ni estado.")
    @ApiResponse(responseCode = "409", description = "Correo duplicado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioActualizarRequest req,
                                      @AuthenticationPrincipal UsuarioAutenticado yo) {
        return usuarioService.actualizar(id, req, yo);
    }

    @PatchMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cambiar rol", description = "Solo Administrador. No puede cambiar su propio rol (409).")
    public UsuarioResponse cambiarRol(@PathVariable Long id, @Valid @RequestBody CambioRolRequest req,
                                      @AuthenticationPrincipal UsuarioAutenticado yo) {
        return usuarioService.cambiarRol(id, req, yo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar (desactivar) usuario", description = "Solo Administrador. Marca activo=false; la cuenta ya no puede iniciar sesión y sus tokens dejan de servir.")
    @ApiResponse(responseCode = "204", description = "Usuario desactivado")
    public void desactivar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado yo) {
        usuarioService.desactivar(id, yo);
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Reactivar usuario", description = "Solo Administrador.")
    public UsuarioResponse reactivar(@PathVariable Long id) {
        return usuarioService.reactivar(id);
    }
}
