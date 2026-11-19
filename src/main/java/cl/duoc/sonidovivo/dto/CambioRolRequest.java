package cl.duoc.sonidovivo.dto;

import cl.duoc.sonidovivo.model.NombreRol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CambioRolRequest(
        @Schema(example = "VENDEDOR")
        @NotNull(message = "El rol es obligatorio")
        NombreRol rol
) { }
