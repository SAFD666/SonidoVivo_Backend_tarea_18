package cl.duoc.sonidovivo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;
import java.util.Locale;

// Roles de Sonido Vivo serializados en JSON como en React ("Administrador", "Vendedor", "Cliente"); en BD y Spring Security usan el enum.
public enum NombreRol {
    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor"),
    CLIENTE("Cliente");

    private final String etiqueta;

    NombreRol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @JsonValue
    public String getEtiqueta() {
        return etiqueta;
    }

    // Valida roles aceptando variaciones de mayusculas ("Administrador", "ADMINISTRADOR"); rechaza cualquier otro con 400.
    @JsonCreator
    public static NombreRol desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
        for (NombreRol r : values()) {
            if (r.name().equals(limpio)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + texto);
    }
}
