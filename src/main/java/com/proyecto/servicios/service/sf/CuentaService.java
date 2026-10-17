package com.proyecto.servicios.service.sf;

import com.proyecto.servicios.model.sf.CuentaEstatusUpdateRequest;
import com.proyecto.servicios.model.sf.CuentaNumeroRequest;
import com.proyecto.servicios.model.sf.CuentaRequest;
import com.proyecto.servicios.model.sf.CuentaResponse;
import com.proyecto.servicios.model.sf.CuentaSearchRequest;

import java.util.List;

public interface CuentaService {
    CuentaResponse crearCuenta(CuentaRequest request);
    List<CuentaResponse> buscarCuentas(CuentaSearchRequest request);
    CuentaResponse consultarPorNumero(CuentaNumeroRequest request);
    CuentaResponse consultarPorNumero(String numeroCuenta);
    CuentaResponse actualizarEstatus(CuentaEstatusUpdateRequest request);
    CuentaResponse actualizarEstatus(String numeroCuenta, String estatus);
    List<CuentaResponse> consultarPorClienteId(Long clienteId);
    List<CuentaResponse> buscarPorEstatus(String estatus);
}
