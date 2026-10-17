package com.proyecto.servicios.service.sf.Impl;

import com.proyecto.servicios.entity.sf.Cliente;
import com.proyecto.servicios.entity.sf.Cuenta;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.ValidationException;
import com.proyecto.servicios.model.sf.CuentaEstatusUpdateRequest;
import com.proyecto.servicios.model.sf.CuentaNumeroRequest;
import com.proyecto.servicios.model.sf.CuentaRequest;
import com.proyecto.servicios.model.sf.CuentaResponse;
import com.proyecto.servicios.model.sf.CuentaSearchRequest;
import com.proyecto.servicios.repositorys.sf.ClienteRepository;
import com.proyecto.servicios.repositorys.sf.CuentaRepository;
import com.proyecto.servicios.repositorys.sf.CuentaSpecification;
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
    @Transactional(readOnly = true)
    public List<CuentaResponse> buscarCuentas(CuentaSearchRequest request) {
        return cuentaRepository.findAll(CuentaSpecification.conFiltros(request))
                .stream().map(CuentaResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponse consultarPorNumero(CuentaNumeroRequest request) {
        return consultarPorNumero(request.getNumeroCuenta());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponse consultarPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));
        return new CuentaResponse(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> consultarPorClienteId(Long clienteId) {
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        if (cuentas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron cuentas para el cliente con ID: " + clienteId);
        }
        return cuentas.stream().map(CuentaResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> buscarPorEstatus(String estatus) {
        List<Cuenta> cuentas = cuentaRepository.findByEstatusIgnoreCase(estatus);
        if (cuentas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron cuentas con el estatus: " + estatus);
        }
        return cuentas.stream().map(CuentaResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuentaResponse crearCuenta(CuentaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.getClienteId()));

        if (cliente.getActivo() != null && !cliente.getActivo()) {
            throw new ValidationException("No se pueden crear cuentas para clientes inactivos.");
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setCliente(cliente);
        cuenta.setNumeroCuenta(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
        cuenta.setSaldo(new BigDecimal("0.00"));
        cuenta.setEstatus("ACTIVA");

        return new CuentaResponse(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaResponse actualizarEstatus(CuentaEstatusUpdateRequest request) {
        return actualizarEstatus(request.getNumeroCuenta(), request.getEstatus());
    }

    @Override
    @Transactional
    public CuentaResponse actualizarEstatus(String numeroCuenta, String estatus) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));
        
        cuenta.setEstatus(estatus.toUpperCase());
        return new CuentaResponse(cuentaRepository.save(cuenta));
    }
}
