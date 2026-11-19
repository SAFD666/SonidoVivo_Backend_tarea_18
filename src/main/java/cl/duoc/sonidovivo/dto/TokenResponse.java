package cl.duoc.sonidovivo.dto;

// Respuesta del login: token, tipo, vigencia y datos públicos del usuario.
public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UsuarioResponse usuario
) { }
