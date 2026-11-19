package cl.duoc.sonidovivo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Tabla "usuario": correo unico y contrasena solo como hash BCrypt; eliminar desactiva (activo = false) para preservar pedidos asociados.
@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_correo", columnNames = "correo"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String correo;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_rol"))
    private Rol rol;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
