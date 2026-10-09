package com.proyecto.servicios.model.sf;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClienteUpdateRequest {
    private String nombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String sexo;
    private Long paisNacionalidadId;
    private String estadoCivil;
    private String correoElectronico;
    private Long telefonoMovil;
    private Long telefonoAlternativo;
    private String ocupacion;
    private String empresa;
    private BigDecimal ingresoMensual;
    private DomicilioRequest domicilio;
}
