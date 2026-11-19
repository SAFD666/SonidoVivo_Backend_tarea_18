package cl.duoc.sonidovivo.config;

import cl.duoc.sonidovivo.model.NombreRol;
import cl.duoc.sonidovivo.model.Rol;
import cl.duoc.sonidovivo.model.Usuario;
import cl.duoc.sonidovivo.repository.RolRepository;
import cl.duoc.sonidovivo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga los datos iniciales SOLO si faltan (no duplica nada al reiniciar):
 *  1. Los tres roles.
 *  2. Cuentas de demostración si se definió DEMO_PASSWORD.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String demoPassword;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.seed.demo-password:}") String demoPassword) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.demoPassword = demoPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearRolSiFalta(NombreRol.ADMINISTRADOR, "Gestiona usuarios, roles, catálogo y pedidos");
        crearRolSiFalta(NombreRol.VENDEDOR, "Gestiona productos, categorías, stock y estados de pedidos");
        crearRolSiFalta(NombreRol.CLIENTE, "Compra productos y ve solo sus pedidos");

        if (demoPassword == null || demoPassword.isBlank()) {
            log.warn("DEMO_PASSWORD no está definido: no se crean cuentas de demostración.");
            return;
        }
        crearUsuarioSiFalta("Carlos Dueño (Admin)", "admin@sonidovivo.cl", NombreRol.ADMINISTRADOR);
        crearUsuarioSiFalta("Matías Vendedor", "vendedor@sonidovivo.cl", NombreRol.VENDEDOR);
        crearUsuarioSiFalta("Cliente Prueba", "cliente@correo.cl", NombreRol.CLIENTE);
    }

    private void crearRolSiFalta(NombreRol nombre, String descripcion) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            rolRepository.save(new Rol(nombre, descripcion));
        }
    }

    private void crearUsuarioSiFalta(String nombre, String correo, NombreRol nombreRol) {
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            return;
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setPasswordHash(passwordEncoder.encode(demoPassword));
        u.setRol(rolRepository.findByNombre(nombreRol).orElseThrow());
        usuarioRepository.save(u);
        log.info("Usuario de demostración creado: {} ({})", correo, nombreRol);
    }
}
