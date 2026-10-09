package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CuentaRequest {
    @NotNull
    private Long clienteId;
}
