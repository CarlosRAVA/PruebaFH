package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class CuentaNumeroRequest {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    public CuentaNumeroRequest() {
    }

    @Builder
    public CuentaNumeroRequest(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
}
