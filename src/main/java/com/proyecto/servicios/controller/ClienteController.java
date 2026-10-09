package com.proyecto.servicios.controller;

import com.proyecto.servicios.model.sf.ClienteRequest;
import com.proyecto.servicios.model.sf.ClienteResponse;
import com.proyecto.servicios.model.sf.ClienteUpdateRequest;
import com.proyecto.servicios.service.sf.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> registrarCliente(@Valid @RequestBody ClienteRequest request) {
        return new ResponseEntity<>(clienteService.registrarCliente(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> consultarTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidoPaterno,
            @RequestParam(required = false) String apellidoMaterno,
            @RequestParam(required = false) String curp,
            @RequestParam(required = false) String rfc,
            @RequestParam(required = false) String correo) {
        
        if (curp != null) {
            return ResponseEntity.ok(List.of(clienteService.consultarPorCurp(curp)));
        }
        if (rfc != null) {
            return ResponseEntity.ok(List.of(clienteService.consultarPorRfc(rfc)));
        }
        if (correo != null) {
            return ResponseEntity.ok(List.of(clienteService.consultarPorCorreo(correo)));
        }
        if (nombre != null) {
            return ResponseEntity.ok(clienteService.buscarPorNombre(nombre));
        }
        if (apellidoPaterno != null) {
            return ResponseEntity.ok(clienteService.buscarPorApellidoPaterno(apellidoPaterno));
        }
        if (apellidoMaterno != null) {
            return ResponseEntity.ok(clienteService.buscarPorApellidoMaterno(apellidoMaterno));
        }

        return ResponseEntity.ok(clienteService.consultarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ClienteResponse>> consultarActivos() {
        return ResponseEntity.ok(clienteService.consultarActivos());
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<ClienteResponse>> consultarPorRangoFechas(
            @RequestParam org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime inicio,
            @RequestParam org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fin) {
        return ResponseEntity.ok(clienteService.consultarPorRangoFechas(inicio, fin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> consultarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.consultarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarParcial(@PathVariable Long id, @RequestBody ClienteUpdateRequest request) {
        return ResponseEntity.ok(clienteService.actualizarParcial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> bajaLogica(@PathVariable Long id) {
        clienteService.bajaLogica(id);
        return ResponseEntity.noContent().build();
    }
}
