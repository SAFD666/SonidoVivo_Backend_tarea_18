package cl.duoc.sonidovivo.repository;

import cl.duoc.sonidovivo.model.NombreRol;
import cl.duoc.sonidovivo.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(NombreRol nombre);
}
