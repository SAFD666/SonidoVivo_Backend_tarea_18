package cl.duoc.sonidovivo.security;

import cl.duoc.sonidovivo.model.Usuario;
import cl.duoc.sonidovivo.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// Filtro por solicitud que lee "Authorization: Bearer <token>", lo valida y autentica en Spring Security; si falta, esta alterado o vencio no autentica y deja que Spring responda 401 en rutas protegidas.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR = "jwt_error";
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(PREFIJO)) {
            String token = header.substring(PREFIJO.length()).trim();
            try {
                Claims claims = jwtService.validar(token);
                Long usuarioId = Long.valueOf(claims.getSubject());

                // Se consulta la base en cada solicitud: si la cuenta fue desactivada o cambió de rol,
                // el cambio se aplica de inmediato aunque el token todavía no haya vencido.
                Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
                if (usuario.isPresent() && usuario.get().isActivo()) {
                    Usuario u = usuario.get();
                    UsuarioAutenticado principal = new UsuarioAutenticado(u.getId(), u.getCorreo(), u.getRol().getNombre());
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().getNombre().name()));
                    var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    request.setAttribute(ATRIBUTO_ERROR, "La cuenta no existe o está desactivada");
                }
            } catch (ExpiredJwtException e) {
                request.setAttribute(ATRIBUTO_ERROR, "El token venció; inicia sesión nuevamente");
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(ATRIBUTO_ERROR, "Token inválido");
            }
        }
        chain.doFilter(request, response);
    }
}
