package cl.duoc.sonidovivo.security;

import cl.duoc.sonidovivo.model.NombreRol;

// Identidad validada desde el JWT inyectada en controladores via @AuthenticationPrincipal; el id proviene del token firmado, nunca de datos enviados por el cliente.
public record UsuarioAutenticado(Long id, String correo, NombreRol rol) {

    public boolean esAdministrador() {
        return rol == NombreRol.ADMINISTRADOR;
    }

    // Administrador o Vendedor: roles con acceso al panel de administracion del frontend.
    public boolean esPersonal() {
        return rol == NombreRol.ADMINISTRADOR || rol == NombreRol.VENDEDOR;
    }
}
