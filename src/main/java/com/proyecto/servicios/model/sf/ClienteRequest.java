package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClienteRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String nombre;

    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z\\s]*$")
    private String segundoNombre;

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String apellidoPaterno;

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String apellidoMaterno;

    @NotNull
    @Past
    private LocalDate fechaNacimiento;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}\\d{6}[HM][A-Z]{5}[A-Z0-9]\\d$")
    private String curp;

    @NotBlank
    @Pattern(regexp = "^[A-Z&Ñ]{3,4}\\d{6}[A-V1-9][A-Z1-9][0-9A]$")
    private String rfc;

    @NotBlank
    private String sexo;

    @NotNull
    private Long paisNacionalidadId;

    @NotBlank
    private String estadoCivil;

    @NotBlank
    @Email
    @Size(max = 100)
    private String correoElectronico;

    @NotNull
    @Min(1000000000L)
    @Max(9999999999L)
    private Long telefonoMovil;

    @Min(1000000000L)
    @Max(9999999999L)
    private Long telefonoAlternativo;

    @NotBlank
    private String ocupacion;

    @NotBlank
    private String empresa;

    @NotNull
    @Positive
    private BigDecimal ingresoMensual;

    @NotNull
    private DomicilioRequest domicilio;
    
    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")
    private String password;
}
