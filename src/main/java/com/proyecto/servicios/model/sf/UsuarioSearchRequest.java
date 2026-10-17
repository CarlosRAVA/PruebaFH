package com.proyecto.servicios.model.sf;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioSearchRequest {

    private Long id;
    private String correo;
    private Boolean activo;
    private String rol;
    private Long clienteId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public UsuarioSearchRequest() {
    }

    @Builder
    public UsuarioSearchRequest(Long id, String correo, Boolean activo, String rol, Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.id = id;
        this.correo = correo;
        this.activo = activo;
        this.rol = rol;
        this.clienteId = clienteId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}
