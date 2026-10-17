package com.proyecto.servicios.controller;

import com.proyecto.servicios.model.common.ApiResponse;
import com.proyecto.servicios.model.sf.CuentaEstatusUpdateRequest;
import com.proyecto.servicios.model.sf.CuentaNumeroRequest;
import com.proyecto.servicios.model.sf.CuentaRequest;
import com.proyecto.servicios.model.sf.CuentaResponse;
import com.proyecto.servicios.model.sf.CuentaSearchRequest;
import com.proyecto.servicios.service.sf.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@Tag(name = "Cuentas", description = "Endpoints para la administración y consulta de cuentas bancarias")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping
    @Operation(summary = "Crear nueva cuenta para un cliente activo")
    public ResponseEntity<ApiResponse<CuentaResponse>> crearCuenta(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse nuevaCuenta = cuentaService.crearCuenta(request);
        return new ResponseEntity<>(ApiResponse.success(nuevaCuenta, "Cuenta creada exitosamente."), HttpStatus.CREATED);
    }

    @PostMapping("/buscar")
    @Operation(summary = "Búsqueda dinámica de cuentas combinando filtros")
    public ResponseEntity<ApiResponse<List<CuentaResponse>>> buscarCuentas(@Valid @RequestBody(required = false) CuentaSearchRequest request) {
        List<CuentaResponse> cuentas = cuentaService.buscarCuentas(request);
        return ResponseEntity.ok(ApiResponse.success(cuentas, "Consulta de cuentas realizada con éxito. Total: " + cuentas.size()));
    }

    @PostMapping("/detalle")
    @Operation(summary = "Consultar detalle de una cuenta por número de cuenta")
    public ResponseEntity<ApiResponse<CuentaResponse>> consultarPorNumero(@Valid @RequestBody CuentaNumeroRequest request) {
        CuentaResponse cuenta = cuentaService.consultarPorNumero(request);
        return ResponseEntity.ok(ApiResponse.success(cuenta, "Cuenta obtenida exitosamente."));
    }

    @PatchMapping("/estatus")
    @Operation(summary = "Actualizar estatus de una cuenta")
    public ResponseEntity<ApiResponse<CuentaResponse>> actualizarEstatus(@Valid @RequestBody CuentaEstatusUpdateRequest request) {
        CuentaResponse cuentaActualizada = cuentaService.actualizarEstatus(request);
        return ResponseEntity.ok(ApiResponse.success(cuentaActualizada, "Estatus de cuenta actualizado exitosamente."));
    }
}
