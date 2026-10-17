package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
public class UsuarioStatusRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long id;

    @NotNull(message = "El estado activo es obligatorio (true o false)")
    private Boolean activo;

    public UsuarioStatusRequest() {
    }

    @Builder
    public UsuarioStatusRequest(Long id, Boolean activo) {
        this.id = id;
        this.activo = activo;
    }
}
