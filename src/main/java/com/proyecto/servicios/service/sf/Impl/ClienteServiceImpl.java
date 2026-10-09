package com.proyecto.servicios.service.sf.Impl;

import com.proyecto.servicios.entity.sf.Cliente;
import com.proyecto.servicios.entity.sf.Cuenta;
import com.proyecto.servicios.entity.sf.Domicilio;
import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.exceptions.DuplicateResourceException;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.ValidationException;
import com.proyecto.servicios.model.sf.ClienteRequest;
import com.proyecto.servicios.model.sf.ClienteResponse;
import com.proyecto.servicios.model.sf.ClienteUpdateRequest;
import com.proyecto.servicios.entity.sf.CatPais;
import com.proyecto.servicios.entity.sf.CatEstado;
import com.proyecto.servicios.entity.sf.CatCodigoPostal;
import com.proyecto.servicios.repositorys.sf.CatPaisRepository;
import com.proyecto.servicios.repositorys.sf.CatEstadoRepository;
import com.proyecto.servicios.repositorys.sf.CatCodigoPostalRepository;
import com.proyecto.servicios.repositorys.sf.ClienteRepository;
import com.proyecto.servicios.repositorys.sf.CuentaRepository;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import com.proyecto.servicios.service.sf.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CatPaisRepository catPaisRepository;

    @Autowired
    private CatEstadoRepository catEstadoRepository;

    @Autowired
    private CatCodigoPostalRepository catCodigoPostalRepository;

    @Override
    @Transactional
    public ClienteResponse registrarCliente(ClienteRequest request) {
        validarEdad(request.getFechaNacimiento());
        if (clienteRepository.findByCurp(request.getCurp()).isPresent()) {
            throw new DuplicateResourceException("La CURP ya está registrada.");
        }
        if (clienteRepository.findByRfc(request.getRfc()).isPresent()) {
            throw new DuplicateResourceException("El RFC ya está registrado.");
        }
        if (clienteRepository.findByCorreoElectronico(request.getCorreoElectronico()).isPresent()) {
            throw new DuplicateResourceException("El correo electrónico ya está registrado.");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setSegundoNombre(request.getSegundoNombre());
        cliente.setApellidoPaterno(request.getApellidoPaterno());
        cliente.setApellidoMaterno(request.getApellidoMaterno());
        cliente.setFechaNacimiento(request.getFechaNacimiento());
        cliente.setCurp(request.getCurp());
        cliente.setRfc(request.getRfc());
        cliente.setSexo(request.getSexo());
        CatPais pais = catPaisRepository.findById(request.getPaisNacionalidadId())
                .orElseThrow(() -> new ResourceNotFoundException("País no encontrado con id: " + request.getPaisNacionalidadId()));
        cliente.setPaisNacionalidad(pais);
        cliente.setEstadoCivil(request.getEstadoCivil());
        cliente.setCorreoElectronico(request.getCorreoElectronico());
        cliente.setTelefonoMovil(request.getTelefonoMovil());
        cliente.setTelefonoAlternativo(request.getTelefonoAlternativo());
        cliente.setOcupacion(request.getOcupacion());
        cliente.setEmpresa(request.getEmpresa());
        cliente.setIngresoMensual(request.getIngresoMensual());
        cliente.setActivo(true);

        Domicilio domicilio = new Domicilio();
        domicilio.setCliente(cliente);
        domicilio.setCalle(request.getDomicilio().getCalle());
        domicilio.setNumeroExterior(request.getDomicilio().getNumeroExterior());
        domicilio.setNumeroInterior(request.getDomicilio().getNumeroInterior());
        domicilio.setColonia(request.getDomicilio().getColonia());
        domicilio.setMunicipio(request.getDomicilio().getMunicipio());
        CatEstado estado = catEstadoRepository.findById(request.getDomicilio().getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con id: " + request.getDomicilio().getEstadoId()));
        domicilio.setEstado(estado);

        CatCodigoPostal cp = catCodigoPostalRepository.findByCodigo(request.getDomicilio().getCodigoPostal())
                .orElseGet(() -> {
                    CatCodigoPostal nuevoCp = new CatCodigoPostal();
                    nuevoCp.setCodigo(request.getDomicilio().getCodigoPostal());
                    return catCodigoPostalRepository.save(nuevoCp);
                });
        domicilio.setCodigoPostal(cp);

        cliente.setDomicilio(domicilio);
        
        Cliente savedCliente = clienteRepository.save(cliente);

        Cuenta cuenta = new Cuenta();
        cuenta.setCliente(savedCliente);
        cuenta.setNumeroCuenta(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10)); // simple unique account num
        cuenta.setSaldo(new BigDecimal("1000.00")); // Saldo inicial
        cuenta.setEstatus("ACTIVA");
        cuentaRepository.save(cuenta);

        Usuario usuario = new Usuario();
        usuario.setCliente(savedCliente);
        usuario.setCorreo(savedCliente.getCorreoElectronico());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        return new ClienteResponse(savedCliente);
    }

    @Override
    public List<ClienteResponse> consultarTodos() {
        return clienteRepository.findAll().stream().map(ClienteResponse::new).collect(Collectors.toList());
    }

    @Override
    public ClienteResponse consultarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return new ClienteResponse(cliente);
    }

    @Override
    public List<ClienteResponse> buscarPorNombre(String nombre) {
        List<Cliente> clientes = clienteRepository.findByNombreContainingIgnoreCase(nombre);
        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron clientes con el nombre: " + nombre);
        }
        return clientes.stream().map(ClienteResponse::new).collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponse> buscarPorApellidoPaterno(String apellidoPaterno) {
        List<Cliente> clientes = clienteRepository.findByApellidoPaternoContainingIgnoreCase(apellidoPaterno);
        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron clientes con el apellido paterno: " + apellidoPaterno);
        }
        return clientes.stream().map(ClienteResponse::new).collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponse> buscarPorApellidoMaterno(String apellidoMaterno) {
        List<Cliente> clientes = clienteRepository.findByApellidoMaternoContainingIgnoreCase(apellidoMaterno);
        if (clientes.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron clientes con el apellido materno: " + apellidoMaterno);
        }
        return clientes.stream().map(ClienteResponse::new).collect(Collectors.toList());
    }

    @Override
    public ClienteResponse consultarPorCurp(String curp) {
        Cliente cliente = clienteRepository.findByCurp(curp)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con CURP: " + curp));
        return new ClienteResponse(cliente);
    }

    @Override
    public ClienteResponse consultarPorRfc(String rfc) {
        Cliente cliente = clienteRepository.findByRfc(rfc)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con RFC: " + rfc));
        return new ClienteResponse(cliente);
    }

    @Override
    public ClienteResponse consultarPorCorreo(String correo) {
        Cliente cliente = clienteRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con correo: " + correo));
        return new ClienteResponse(cliente);
    }

    @Override
    public List<ClienteResponse> consultarActivos() {
        return clienteRepository.findByActivoTrue().stream().map(ClienteResponse::new).collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponse> consultarPorRangoFechas(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return clienteRepository.findByFechaCreacionBetween(start, end).stream().map(ClienteResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteResponse actualizarParcial(Long id, ClienteUpdateRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (request.getNombre() != null) cliente.setNombre(request.getNombre());
        if (request.getSegundoNombre() != null) cliente.setSegundoNombre(request.getSegundoNombre());
        if (request.getApellidoPaterno() != null) cliente.setApellidoPaterno(request.getApellidoPaterno());
        if (request.getApellidoMaterno() != null) cliente.setApellidoMaterno(request.getApellidoMaterno());
        if (request.getFechaNacimiento() != null) {
            validarEdad(request.getFechaNacimiento());
            cliente.setFechaNacimiento(request.getFechaNacimiento());
        }
        if (request.getSexo() != null) cliente.setSexo(request.getSexo());
        if (request.getPaisNacionalidadId() != null) {
            CatPais pais = catPaisRepository.findById(request.getPaisNacionalidadId())
                    .orElseThrow(() -> new ResourceNotFoundException("País no encontrado con id: " + request.getPaisNacionalidadId()));
            cliente.setPaisNacionalidad(pais);
        }
        if (request.getEstadoCivil() != null) cliente.setEstadoCivil(request.getEstadoCivil());
        if (request.getCorreoElectronico() != null && !request.getCorreoElectronico().equals(cliente.getCorreoElectronico())) {
            if (clienteRepository.findByCorreoElectronico(request.getCorreoElectronico()).isPresent()) {
                throw new DuplicateResourceException("El correo electrónico ya está registrado.");
            }
            cliente.setCorreoElectronico(request.getCorreoElectronico());
        }
        if (request.getTelefonoMovil() != null) cliente.setTelefonoMovil(request.getTelefonoMovil());
        if (request.getTelefonoAlternativo() != null) cliente.setTelefonoAlternativo(request.getTelefonoAlternativo());
        if (request.getOcupacion() != null) cliente.setOcupacion(request.getOcupacion());
        if (request.getEmpresa() != null) cliente.setEmpresa(request.getEmpresa());
        if (request.getIngresoMensual() != null) {
            if (request.getIngresoMensual().compareTo(BigDecimal.ZERO) <= 0) {
                 throw new ValidationException("Ingreso mensual debe ser mayor a cero.");
            }
            cliente.setIngresoMensual(request.getIngresoMensual());
        }

        if (request.getDomicilio() != null) {
            Domicilio d = cliente.getDomicilio();
            if (request.getDomicilio().getCalle() != null) d.setCalle(request.getDomicilio().getCalle());
            if (request.getDomicilio().getNumeroExterior() != null) d.setNumeroExterior(request.getDomicilio().getNumeroExterior());
            if (request.getDomicilio().getNumeroInterior() != null) d.setNumeroInterior(request.getDomicilio().getNumeroInterior());
            if (request.getDomicilio().getColonia() != null) d.setColonia(request.getDomicilio().getColonia());
            if (request.getDomicilio().getMunicipio() != null) d.setMunicipio(request.getDomicilio().getMunicipio());
            if (request.getDomicilio().getEstadoId() != null) {
                CatEstado estado = catEstadoRepository.findById(request.getDomicilio().getEstadoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con id: " + request.getDomicilio().getEstadoId()));
                d.setEstado(estado);
            }
            if (request.getDomicilio().getCodigoPostal() != null) {
                CatCodigoPostal cp = catCodigoPostalRepository.findByCodigo(request.getDomicilio().getCodigoPostal())
                        .orElseGet(() -> {
                            CatCodigoPostal nuevoCp = new CatCodigoPostal();
                            nuevoCp.setCodigo(request.getDomicilio().getCodigoPostal());
                            return catCodigoPostalRepository.save(nuevoCp);
                        });
                d.setCodigoPostal(cp);
            }
        }

        return new ClienteResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void bajaLogica(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        cliente.setActivo(false);
        clienteRepository.save(cliente);

        // Desactivar usuario
        usuarioRepository.findByCorreo(cliente.getCorreoElectronico()).ifPresent(usuario -> {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        });

        // Desactivar cuentas asociadas
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(id);
        for (Cuenta cuenta : cuentas) {
            cuenta.setEstatus("INACTIVA");
            cuentaRepository.save(cuenta);
        }
    }

    private void validarEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) throw new ValidationException("Fecha de nacimiento es requerida.");
        if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18) {
            throw new ValidationException("El cliente debe ser mayor de edad.");
        }
    }
}
