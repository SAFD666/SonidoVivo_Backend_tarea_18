package cl.duoc.sonidovivo.dto;

import cl.duoc.sonidovivo.model.NombreRol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// Modificación de usuario (los campos nulos no cambian; "rol" y "activo" solo para Admins).
public record UsuarioActualizarRequest(
        @Schema(example = "Ana María Pérez")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @Schema(example = "ana.perez@correo.cl")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120)
        String correo,

        @Schema(example = "otraClave456")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password,

        @Schema(example = "Av. Libertad 450, Viña del Mar")
        @Size(max = 200)
        String direccion,

        @Schema(example = "Vendedor", description = "Solo Administrador")
        NombreRol rol,

        @Schema(description = "Solo Administrador")
        Boolean activo
) { }
