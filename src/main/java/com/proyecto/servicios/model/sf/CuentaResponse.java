package com.proyecto.servicios.model.sf;

import com.proyecto.servicios.entity.sf.Cuenta;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CuentaResponse {
    private Long id;
    private Long clienteId;
    private String numeroCuenta;
    private BigDecimal saldo;
    private String estatus;
    private LocalDateTime fechaCreacion;

    public CuentaResponse(Cuenta c) {
        this.id = c.getId();
        this.clienteId = c.getCliente().getId();
        this.numeroCuenta = c.getNumeroCuenta();
        this.saldo = c.getSaldo();
        this.estatus = c.getEstatus();
        this.fechaCreacion = c.getFechaCreacion();
    }
}
