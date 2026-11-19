package cl.duoc.sonidovivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "cliente@sonidovivo.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo,

        @Schema(example = "Demo2026!")
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) { }
