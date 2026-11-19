package cl.duoc.sonidovivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

// Datos de registro del frontend (el servidor asigna el rol de Cliente automáticamente).
public record RegistroRequest(
        @Schema(example = "Ana Pérez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @Schema(example = "ana.perez@correo.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120)
        String correo,

        @Schema(example = "clave123")
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password
) { }
