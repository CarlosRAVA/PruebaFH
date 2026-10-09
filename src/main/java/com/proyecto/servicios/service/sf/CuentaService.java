package com.proyecto.servicios.service.sf;

import com.proyecto.servicios.model.sf.CuentaRequest;
import com.proyecto.servicios.model.sf.CuentaResponse;
import java.util.List;

public interface CuentaService {
    CuentaResponse consultarPorNumero(String numeroCuenta);
    List<CuentaResponse> consultarPorClienteId(Long clienteId);
    List<CuentaResponse> buscarPorEstatus(String estatus);
    CuentaResponse crearCuenta(CuentaRequest request);
    CuentaResponse actualizarEstatus(String numeroCuenta, String estatus);
}
