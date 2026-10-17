package com.proyecto.servicios.model.sf;

import lombok.Builder;
import lombok.Data;

@Data
public class CuentaSearchRequest {

    private Long clienteId;
    private String estatus;
    private String numeroCuenta;

    public CuentaSearchRequest() {
    }

    @Builder
    public CuentaSearchRequest(Long clienteId, String estatus, String numeroCuenta) {
        this.clienteId = clienteId;
        this.estatus = estatus;
        this.numeroCuenta = numeroCuenta;
    }
}
