package com.proyecto.servicios.model.sf;

import com.proyecto.servicios.entity.sf.Usuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse {

    private Long id;
    private String correo;
    private String rol;
    private Boolean activo;
    private Long clienteId;
    private String clienteNombreCompleto;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public UsuarioResponse() {
    }

    @Builder
    public UsuarioResponse(Long id, String correo, String rol, Boolean activo, Long clienteId, String clienteNombreCompleto, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
        this.clienteId = clienteId;
        this.clienteNombreCompleto = clienteNombreCompleto;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public UsuarioResponse(Usuario u) {
        if (u != null) {
            this.id = u.getId();
            this.correo = u.getCorreo();
            this.rol = u.getRol();
            this.activo = u.getActivo();
            if (u.getCliente() != null) {
                this.clienteId = u.getCliente().getId();
                String nombre = u.getCliente().getNombre() != null ? u.getCliente().getNombre() : "";
                String apellido = u.getCliente().getApellidoPaterno() != null ? u.getCliente().getApellidoPaterno() : "";
                this.clienteNombreCompleto = (nombre + " " + apellido).trim();
            }
            this.fechaCreacion = u.getFechaCreacion();
            this.fechaActualizacion = u.getFechaActualizacion();
        }
    }
}
