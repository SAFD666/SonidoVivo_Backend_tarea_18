package cl.duoc.sonidovivo.security;

import cl.duoc.sonidovivo.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

// Emite y verifica JWT de acceso con HMAC-SHA256 (HS256) y clave JWT_SECRET; claims sub (id usuario), iss, aud, iat, exp y rol, sin contrasenas ni datos sensibles.
@Service
public class JwtService {

    private static final String ALGORITMO_PERMITIDO = "HS256";

    private final SecretKey clave;
    private final long vigenciaSegundos;
    private final String emisor;
    private final String destinatario;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.expiration-minutes}") long vigenciaMinutos,
                      @Value("${jwt.issuer}") String emisor,
                      @Value("${jwt.audience}") String destinatario) {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres. Revisa tus variables de entorno.");
        }
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.vigenciaSegundos = vigenciaMinutos * 60;
        this.emisor = emisor;
        this.destinatario = destinatario;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .issuer(emisor)
                .audience().add(destinatario).and()
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(vigenciaSegundos)))
                .claim("rol", usuario.getRol().getNombre().getEtiqueta())
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    // Valida firma, algoritmo, expiracion, emisor y destinatario (no basta decodificar); lanza JwtException o ExpiredJwtException si falla.
    public Claims validar(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(clave)
                .requireIssuer(emisor)
                .requireAudience(destinatario)
                .build()
                .parseSignedClaims(token);
        if (!ALGORITMO_PERMITIDO.equals(jws.getHeader().getAlgorithm())) {
            throw new JwtException("Algoritmo de firma no permitido");
        }
        return jws.getPayload();
    }

    public long getVigenciaSegundos() {
        return vigenciaSegundos;
    }
}
