package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
public class ClienteIdRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser un número positivo")
    private Long id;

    public ClienteIdRequest() {
    }

    @Builder
    public ClienteIdRequest(Long id) {
        this.id = id;
    }
}
