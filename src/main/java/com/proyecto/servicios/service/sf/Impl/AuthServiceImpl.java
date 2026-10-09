package com.proyecto.servicios.service.sf.Impl;

import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.ValidationException;
import com.proyecto.servicios.model.sf.AuthRequest;
import com.proyecto.servicios.model.sf.AuthResponse;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import com.proyecto.servicios.security.JwtUtil;
import com.proyecto.servicios.service.sf.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new ResourceNotFoundException("Credenciales inválidas."));

        if (!usuario.getActivo()) {
            throw new ValidationException("Usuario inactivo.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new ValidationException("Credenciales inválidas.");
        }

        String token = jwtUtil.generateToken(usuario.getCorreo());
        return new AuthResponse(token);
    }
}
