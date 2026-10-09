package com.proyecto.servicios.controller;

import com.proyecto.servicios.model.sf.CuentaRequest;
import com.proyecto.servicios.model.sf.CuentaResponse;
import com.proyecto.servicios.service.sf.CuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponse> crearCuenta(@Valid @RequestBody CuentaRequest request) {
        return new ResponseEntity<>(cuentaService.crearCuenta(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> buscarCuentas(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estatus) {
        if (clienteId != null) {
            return ResponseEntity.ok(cuentaService.consultarPorClienteId(clienteId));
        }
        if (estatus != null) {
            return ResponseEntity.ok(cuentaService.buscarPorEstatus(estatus));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> consultarPorNumero(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(cuentaService.consultarPorNumero(numeroCuenta));
    }

    @PatchMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> actualizarEstatus(@PathVariable String numeroCuenta, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(cuentaService.actualizarEstatus(numeroCuenta, request.get("estatus")));
    }
}
