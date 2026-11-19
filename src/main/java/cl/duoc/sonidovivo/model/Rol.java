package cl.duoc.sonidovivo.model;

import jakarta.persistence.*;

// Tabla "rol": catalogo de roles; cada usuario tiene exactamente un rol.
@Entity
@Table(name = "rol", uniqueConstraints = @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre"))
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NombreRol nombre;

    @Column(length = 150)
    private String descripcion;

    protected Rol() { }

    public Rol(NombreRol nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public NombreRol getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
