package com.proyecto.servicios.service.sf.Impl;

import com.proyecto.servicios.entity.sf.Cliente;
import com.proyecto.servicios.entity.sf.Cuenta;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.ValidationException;
import com.proyecto.servicios.model.sf.CuentaRequest;
import com.proyecto.servicios.model.sf.CuentaResponse;
import com.proyecto.servicios.repositorys.sf.ClienteRepository;
import com.proyecto.servicios.repositorys.sf.CuentaRepository;
import com.proyecto.servicios.service.sf.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CuentaServiceImpl implements CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public CuentaResponse consultarPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));
        return new CuentaResponse(cuenta);
    }

    @Override
    public List<CuentaResponse> consultarPorClienteId(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(CuentaResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CuentaResponse> buscarPorEstatus(String estatus) {
        return cuentaRepository.findByEstatus(estatus).stream()
                .map(CuentaResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuentaResponse crearCuenta(CuentaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + request.getClienteId()));

        if (!cliente.getActivo()) {
            throw new ValidationException("No se pueden crear cuentas para clientes inactivos.");
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setCliente(cliente);
        cuenta.setNumeroCuenta(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        cuenta.setSaldo(new BigDecimal("0.00")); // O un saldo inicial definido por negocio
        cuenta.setEstatus("ACTIVA");

        return new CuentaResponse(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaResponse actualizarEstatus(String numeroCuenta, String estatus) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));
        
        cuenta.setEstatus(estatus);
        return new CuentaResponse(cuentaRepository.save(cuenta));
    }
}
