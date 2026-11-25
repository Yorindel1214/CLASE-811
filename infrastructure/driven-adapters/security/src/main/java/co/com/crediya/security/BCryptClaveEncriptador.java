package co.com.crediya.security;

import co.com.crediya.model.autenticacion.gateways.ClaveEncriptador;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cumple el contrato ClaveEncriptador usando BCrypt.
 */
@Component
public class BCryptClaveEncriptador implements ClaveEncriptador {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encriptar(String claveEnTextoPlano) {
        return encoder.encode(claveEnTextoPlano);
    }

    @Override
    public boolean coincide(String claveEnTextoPlano, String claveEncriptada) {
        return encoder.matches(claveEnTextoPlano, claveEncriptada);
    }
}
