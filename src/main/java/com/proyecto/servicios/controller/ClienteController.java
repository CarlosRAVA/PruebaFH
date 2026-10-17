package com.proyecto.servicios.controller;

import com.proyecto.servicios.model.common.ApiResponse;
import com.proyecto.servicios.model.sf.ClienteIdRequest;
import com.proyecto.servicios.model.sf.ClienteRequest;
import com.proyecto.servicios.model.sf.ClienteResponse;
import com.proyecto.servicios.model.sf.ClienteSearchRequest;
import com.proyecto.servicios.model.sf.ClienteUpdateRequest;
import com.proyecto.servicios.service.sf.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Endpoints para la administración y consulta de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Registrar nuevo cliente con sus cuentas y usuario asociados")
    public ResponseEntity<ApiResponse<ClienteResponse>> registrarCliente(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse nuevoCliente = clienteService.registrarCliente(request);
        return new ResponseEntity<>(ApiResponse.success(nuevoCliente, "Cliente registrado exitosamente."), HttpStatus.CREATED);
    }

    @PostMapping("/buscar")
    @Operation(summary = "Búsqueda dinámica de clientes combinando filtros en un único endpoint")
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> buscarClientes(@Valid @RequestBody(required = false) ClienteSearchRequest request) {
        List<ClienteResponse> clientes = clienteService.buscarClientes(request);
        return ResponseEntity.ok(ApiResponse.success(clientes, "Consulta realizada con éxito. Total encontrados: " + clientes.size()));
    }

    @PostMapping("/detalle")
    @Operation(summary = "Consultar información detallada de un cliente por ID")
    public ResponseEntity<ApiResponse<ClienteResponse>> consultarPorId(@Valid @RequestBody ClienteIdRequest request) {
        ClienteResponse cliente = clienteService.consultarPorId(request);
        return ResponseEntity.ok(ApiResponse.success(cliente, "Cliente obtenido exitosamente."));
    }

    @PutMapping
    @Operation(summary = "Actualizar información de cliente")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizarCliente(@Valid @RequestBody ClienteUpdateRequest request) {
        ClienteResponse actualizado = clienteService.actualizarCliente(request);
        return ResponseEntity.ok(ApiResponse.success(actualizado, "Cliente actualizado exitosamente."));
    }

    @DeleteMapping
    @Operation(summary = "Baja lógica de cliente mediante Request Body")
    public ResponseEntity<ApiResponse<Void>> bajaLogica(@Valid @RequestBody ClienteIdRequest request) {
        clienteService.bajaLogica(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Cliente y cuentas asociadas dadas de baja exitosamente."));
    }
}
