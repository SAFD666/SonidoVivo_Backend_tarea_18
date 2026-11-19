package cl.duoc.sonidovivo.dto;

import cl.duoc.sonidovivo.model.NombreRol;
import cl.duoc.sonidovivo.model.Usuario;

import java.time.LocalDateTime;

// Datos públicos del usuario (sin contraseñas ni hashes).
public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        NombreRol rol,
        String direccion,
        boolean activo,
        LocalDateTime fechaCreacion
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getCorreo(),
                u.getRol().getNombre(),
                u.getDireccion(),
                u.isActivo(),
                u.getFechaCreacion()
        );
    }
}
