package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
public class CuentaEstatusUpdateRequest {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    @NotBlank(message = "El estatus es obligatorio")
    @Pattern(regexp = "^(?i)(ACTIVA|INACTIVA|BLOQUEADA|CANCELADA)$", message = "El estatus debe ser ACTIVA, INACTIVA, BLOQUEADA o CANCELADA")
    private String estatus;

    public CuentaEstatusUpdateRequest() {
    }

    @Builder
    public CuentaEstatusUpdateRequest(String numeroCuenta, String estatus) {
        this.numeroCuenta = numeroCuenta;
        this.estatus = estatus;
    }
}
