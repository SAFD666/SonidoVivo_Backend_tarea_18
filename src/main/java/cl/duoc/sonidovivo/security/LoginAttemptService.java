package cl.duoc.sonidovivo.security;

import cl.duoc.sonidovivo.exception.DemasiadosIntentosException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Limita intentos fallidos de login por correo contra fuerza bruta en memoria (se reinicia al reiniciar el backend, aceptable en esta version).
@Service
public class LoginAttemptService {

    private record Registro(int fallos, Instant bloqueadoHasta) { }

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();
    private final int maxIntentos;
    private final long bloqueoSegundos;

    public LoginAttemptService(@Value("${app.login.max-intentos}") int maxIntentos,
                               @Value("${app.login.bloqueo-minutos}") long bloqueoMinutos) {
        this.maxIntentos = maxIntentos;
        this.bloqueoSegundos = bloqueoMinutos * 60;
    }

    public void verificarNoBloqueado(String correo) {
        Registro r = registros.get(clave(correo));
        if (r != null && r.bloqueadoHasta() != null && Instant.now().isBefore(r.bloqueadoHasta())) {
            throw new DemasiadosIntentosException("Demasiados intentos fallidos. Espera unos minutos e inténtalo de nuevo.");
        }
    }

    public void registrarFallo(String correo) {
        registros.compute(clave(correo), (k, r) -> {
            int fallos = (r == null || r.bloqueadoHasta() != null) ? 1 : r.fallos() + 1;
            Instant bloqueo = fallos >= maxIntentos ? Instant.now().plusSeconds(bloqueoSegundos) : null;
            return new Registro(fallos, bloqueo);
        });
    }

    public void registrarExito(String correo) {
        registros.remove(clave(correo));
    }

    private String clave(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }
}
