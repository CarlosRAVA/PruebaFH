package com.proyecto.servicios.model.sf;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClienteSearchRequest {

    private Long id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String curp;
    private String rfc;
    private String correoElectronico;
    private Boolean activo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public ClienteSearchRequest() {
    }

    @Builder
    public ClienteSearchRequest(Long id, String nombre, String apellidoPaterno, String apellidoMaterno, String curp, String rfc, String correoElectronico, Boolean activo, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.curp = curp;
        this.rfc = rfc;
        this.correoElectronico = correoElectronico;
        this.activo = activo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}
