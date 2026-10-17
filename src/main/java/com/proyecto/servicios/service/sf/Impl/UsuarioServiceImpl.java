package com.proyecto.servicios.service.sf.Impl;

import com.proyecto.servicios.entity.sf.Cliente;
import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.exceptions.BusinessException;
import com.proyecto.servicios.exceptions.DuplicateResourceException;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.UnauthorizedAccessException;
import com.proyecto.servicios.model.sf.UsuarioCreateRequest;
import com.proyecto.servicios.model.sf.UsuarioIdRequest;
import com.proyecto.servicios.model.sf.UsuarioResponse;
import com.proyecto.servicios.model.sf.UsuarioSearchRequest;
import com.proyecto.servicios.model.sf.UsuarioStatusRequest;
import com.proyecto.servicios.model.sf.UsuarioUpdateRequest;
import com.proyecto.servicios.repositorys.sf.ClienteRepository;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import com.proyecto.servicios.repositorys.sf.UsuarioSpecification;
import com.proyecto.servicios.service.sf.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse crearUsuario(UsuarioCreateRequest request) {
        String correoNormalizado = request.getCorreo().trim().toLowerCase();

        if (usuarioRepository.findByCorreo(correoNormalizado).isPresent()) {
            throw new DuplicateResourceException("El correo electrónico " + correoNormalizado + " ya está registrado en el sistema.");
        }

        Cliente cliente = null;
        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.getClienteId()));

            if (cliente.getActivo() != null && !cliente.getActivo()) {
                throw new BusinessException("No es posible vincular un usuario a un cliente inactivo.");
            }

            if (usuarioRepository.existsByClienteId(cliente.getId())) {
                throw new DuplicateResourceException("El cliente con ID " + cliente.getId() + " ya tiene un usuario asociado.");
            }
        }

        Usuario usuario = Usuario.builder()
                .correo(correoNormalizado)
                .password(passwordEncoder.encode(request.getPassword()))
                .cliente(cliente)
                .rol(request.getRol() != null && !request.getRol().isBlank() ? request.getRol().toUpperCase() : "ROLE_USER")
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {} y correo: {}", guardado.getId(), guardado.getCorreo());
        return new UsuarioResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> buscarUsuarios(UsuarioSearchRequest request) {
        Specification<Usuario> spec = UsuarioSpecification.conFiltros(request);
        List<Usuario> usuarios = usuarioRepository.findAll(spec);
        return usuarios.stream().map(UsuarioResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse consultarPorId(UsuarioIdRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getId()));
        return new UsuarioResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse consultarPerfil(String correoAutenticado) {
        if (correoAutenticado == null || correoAutenticado.isBlank()) {
            throw new UnauthorizedAccessException("Se requiere autenticación para acceder al perfil.");
        }
        Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado en el sistema."));
        return new UsuarioResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarUsuario(UsuarioUpdateRequest request, String correoAutenticado) {
        Usuario usuario = usuarioRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getId()));

        validarAutorizacion(usuario, correoAutenticado);

        if (request.getCorreo() != null && !request.getCorreo().trim().isEmpty()) {
            String nuevoCorreo = request.getCorreo().trim().toLowerCase();
            if (!nuevoCorreo.equalsIgnoreCase(usuario.getCorreo())) {
                if (usuarioRepository.findByCorreo(nuevoCorreo).isPresent()) {
                    throw new DuplicateResourceException("El correo electrónico " + nuevoCorreo + " ya está en uso por otro usuario.");
                }
                usuario.setCorreo(nuevoCorreo);
            }
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRol() != null && !request.getRol().trim().isEmpty()) {
            usuario.setRol(request.getRol().toUpperCase());
        }

        if (request.getClienteId() != null) {
            Cliente nuevoCliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.getClienteId()));
            if (!nuevoCliente.getActivo()) {
                throw new BusinessException("No es posible asociar el usuario a un cliente inactivo.");
            }
            usuario.setCliente(nuevoCliente);
        }

        // Los campos como fechaCreacion, id y activo se controlan por el servidor y no se alteran directamente aquí
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario con ID {} actualizado correctamente", usuarioActualizado.getId());
        return new UsuarioResponse(usuarioActualizado);
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstatus(UsuarioStatusRequest request, String correoAutenticado) {
        Usuario usuario = usuarioRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getId()));

        validarAutorizacion(usuario, correoAutenticado);

        usuario.setActivo(request.getActivo());
        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Estatus de usuario ID {} actualizado a activo={}", actualizado.getId(), actualizado.getActivo());
        return new UsuarioResponse(actualizado);
    }

    @Override
    @Transactional
    public void bajaLogica(UsuarioIdRequest request, String correoAutenticado) {
        Usuario usuario = usuarioRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getId()));

        validarAutorizacion(usuario, correoAutenticado);

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Baja lógica aplicada al usuario con ID: {}", usuario.getId());
    }

    private void validarAutorizacion(Usuario targetUser, String correoAutenticado) {
        if (correoAutenticado == null || correoAutenticado.isBlank()) {
            return;
        }

        if (correoAutenticado.equalsIgnoreCase(targetUser.getCorreo())) {
            return;
        }

        Usuario solicitante = usuarioRepository.findByCorreo(correoAutenticado.trim().toLowerCase()).orElse(null);
        if (solicitante != null && "ROLE_ADMIN".equalsIgnoreCase(solicitante.getRol())) {
            return;
        }

        throw new UnauthorizedAccessException("No tiene autorización para acceder o modificar los datos de este usuario.");
    }
}
