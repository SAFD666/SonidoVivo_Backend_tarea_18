package cl.duoc.sonidovivo.service;

import cl.duoc.sonidovivo.dto.LoginRequest;
import cl.duoc.sonidovivo.dto.RegistroRequest;
import cl.duoc.sonidovivo.dto.TokenResponse;
import cl.duoc.sonidovivo.dto.UsuarioResponse;
import cl.duoc.sonidovivo.exception.ConflictoException;
import cl.duoc.sonidovivo.model.NombreRol;
import cl.duoc.sonidovivo.model.Rol;
import cl.duoc.sonidovivo.model.Usuario;
import cl.duoc.sonidovivo.repository.RolRepository;
import cl.duoc.sonidovivo.repository.UsuarioRepository;
import cl.duoc.sonidovivo.security.JwtService;
import cl.duoc.sonidovivo.security.LoginAttemptService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    // Hash ficticio para comparar cuando el correo no existe: iguala el tiempo de respuesta y evita enumerar cuentas.
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       LoginAttemptService loginAttemptService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
        this.hashFicticio = passwordEncoder.encode("contraseña-ficticia-para-igualar-tiempos");
    }

    // Registro publico: el servidor asigna siempre el rol CLIENTE y nadie puede registrarse como administrador.
    @Transactional
    public UsuarioResponse registrar(RegistroRequest req) {
        String correo = normalizarCorreo(req.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("Ya existe una cuenta con ese correo");
        }
        Rol rolCliente = rolRepository.findByNombre(NombreRol.CLIENTE)
                .orElseThrow(() -> new IllegalStateException("El rol CLIENTE no existe en la base de datos"));

        Usuario u = new Usuario();
        u.setNombre(req.nombre().trim());
        u.setCorreo(correo);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRol(rolCliente);
        return UsuarioResponse.desde(usuarioRepository.save(u));
    }

    // Login: busca usuario por correo, valida contrasena con hash BCrypt y emite JWT; ante cualquier fallo responde mensaje generico (401).
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        String correo = normalizarCorreo(req.correo());
        loginAttemptService.verificarNoBloqueado(correo);

        Optional<Usuario> encontrado = usuarioRepository.findByCorreoIgnoreCase(correo);
        String hash = encontrado.map(Usuario::getPasswordHash).orElse(hashFicticio);
        boolean claveCorrecta = passwordEncoder.matches(req.password(), hash);

        if (encontrado.isEmpty() || !claveCorrecta || !encontrado.get().isActivo()) {
            loginAttemptService.registrarFallo(correo);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        loginAttemptService.registrarExito(correo);
        Usuario u = encontrado.get();
        String token = jwtService.generarToken(u);
        return new TokenResponse(token, "Bearer", jwtService.getVigenciaSegundos(), UsuarioResponse.desde(u));
    }

    static String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase(Locale.ROOT);
    }
}
