package co.com.crediya.security;

import co.com.crediya.model.autenticacion.Sesion;
import co.com.crediya.model.autenticacion.gateways.GeneradorToken;
import co.com.crediya.model.usuario.Usuario;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Cumple el contrato GeneradorToken creando un JWT firmado con HS256.
 * El microservicio de Solicitudes valida el token con el mismo secreto (jwt.secreto).
 * Claims: sub = correo, rol, documento y nombre del usuario.
 */
@Slf4j
@Component
public class JwtGeneradorToken implements GeneradorToken {

    private final JwtEncoder encoder;
    private final long minutosDeVigencia;

    public JwtGeneradorToken(@Value("${jwt.secreto}") String secreto,
                             @Value("${jwt.expiracion-minutos:60}") long minutosDeVigencia) {
        SecretKeySpec llave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(llave));
        this.minutosDeVigencia = minutosDeVigencia;
    }

    @Override
    public Sesion generar(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("crediya-autentication-ms")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(minutosDeVigencia, ChronoUnit.MINUTES))
                .subject(usuario.getCorreoElectronico())
                .claim("rol", usuario.getRol().name())
                .claim("documento", usuario.getDocumentoIdentidad() == null ? "" : usuario.getDocumentoIdentidad())
                .claim("nombre", usuario.getNombres() + " " + usuario.getApellidos())
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
        log.info("Token generado para {} con rol {}", usuario.getCorreoElectronico(), usuario.getRol());
        return Sesion.builder()
                .token(token)
                .expiraEnSegundos(minutosDeVigencia * 60)
                .correoElectronico(usuario.getCorreoElectronico())
                .rol(usuario.getRol())
                .build();
    }
}
