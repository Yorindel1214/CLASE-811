package co.com.crediya.jpa;

import co.com.crediya.model.usuario.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "correo_electronico"),
        @UniqueConstraint(columnNames = "documento_identidad")})
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String direccion;
    private String telefono;

    @Column(name = "correo_electronico", nullable = false)
    private String correoElectronico;

    @Column(name = "salario_base", nullable = false)
    private BigDecimal salarioBase;

    // HU3. Se dejan sin "nullable = false" para no romper los usuarios que ya existían de la HU1.
    @Column(name = "documento_identidad", length = 20)
    private String documentoIdentidad;

    /** Clave cifrada con BCrypt (nunca en texto plano). */
    @Column(length = 100)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Rol rol;
}
