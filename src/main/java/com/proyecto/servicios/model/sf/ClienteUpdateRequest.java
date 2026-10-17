package com.proyecto.servicios.model.sf;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClienteUpdateRequest {

    @NotNull(message = "El ID del cliente es obligatorio para la actualización")
    @Positive(message = "El ID del cliente debe ser un número positivo")
    private Long id;

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @Size(max = 50, message = "El segundo nombre no puede exceder 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "El segundo nombre solo puede contener letras y espacios")
    private String segundoNombre;

    @Size(min = 2, max = 50, message = "El apellido paterno debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "El apellido paterno solo puede contener letras y espacios")
    private String apellidoPaterno;

    @Size(min = 2, max = 50, message = "El apellido materno debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "El apellido materno solo puede contener letras y espacios")
    private String apellidoMaterno;

    @Past(message = "La fecha de nacimiento debe corresponder al pasado")
    private LocalDate fechaNacimiento;

    @Pattern(regexp = "^(?i)(Hombre|Mujer|Otro)?$", message = "El sexo debe ser Hombre, Mujer u Otro")
    private String sexo;

    private Long paisNacionalidadId;

    private String estadoCivil;

    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    private String correoElectronico;

    @Min(value = 1000000000L, message = "El teléfono móvil debe tener 10 dígitos")
    @Max(value = 9999999999L, message = "El teléfono móvil debe tener 10 dígitos")
    private Long telefonoMovil;

    @Min(value = 1000000000L, message = "El teléfono alternativo debe tener 10 dígitos")
    @Max(value = 9999999999L, message = "El teléfono alternativo debe tener 10 dígitos")
    private Long telefonoAlternativo;

    private String ocupacion;

    private String empresa;

    @Positive(message = "El ingreso mensual debe ser mayor a cero")
    private BigDecimal ingresoMensual;

    @Valid
    private DomicilioRequest domicilio;

    public ClienteUpdateRequest() {
    }

    @Builder
    public ClienteUpdateRequest(Long id, String nombre, String segundoNombre, String apellidoPaterno, String apellidoMaterno, LocalDate fechaNacimiento, String sexo, Long paisNacionalidadId, String estadoCivil, String correoElectronico, Long telefonoMovil, Long telefonoAlternativo, String ocupacion, String empresa, BigDecimal ingresoMensual, DomicilioRequest domicilio) {
        this.id = id;
        this.nombre = nombre;
        this.segundoNombre = segundoNombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.paisNacionalidadId = paisNacionalidadId;
        this.estadoCivil = estadoCivil;
        this.correoElectronico = correoElectronico;
        this.telefonoMovil = telefonoMovil;
        this.telefonoAlternativo = telefonoAlternativo;
        this.ocupacion = ocupacion;
        this.empresa = empresa;
        this.ingresoMensual = ingresoMensual;
        this.domicilio = domicilio;
    }
}
