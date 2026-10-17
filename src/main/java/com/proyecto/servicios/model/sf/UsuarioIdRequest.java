package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
public class UsuarioIdRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long id;

    public UsuarioIdRequest() {
    }

    @Builder
    public UsuarioIdRequest(Long id) {
        this.id = id;
    }
}
