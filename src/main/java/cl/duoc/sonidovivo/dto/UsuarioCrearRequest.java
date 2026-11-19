package cl.duoc.sonidovivo.dto;

import cl.duoc.sonidovivo.model.NombreRol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

// Alta de usuario por el Administrador (formulario "Nuevo usuario" del frontend).
public record UsuarioCrearRequest(
        @Schema(example = "Pedro Soto")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @Schema(example = "pedro.soto@sonidovivo.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120)
        String correo,

        @Schema(example = "clave123")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password,

        @Schema(example = "Vendedor", description = "Administrador, Vendedor o Cliente")
        @NotNull(message = "El rol es obligatorio")
        NombreRol rol,

        @Schema(example = "true", description = "Si se omite, el usuario queda activo")
        Boolean activo
) { }
