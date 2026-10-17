package com.proyecto.servicios.service.sf.Impl;

import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.exceptions.UnauthorizedAccessException;
import com.proyecto.servicios.model.sf.AuthRequest;
import com.proyecto.servicios.model.sf.AuthResponse;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import com.proyecto.servicios.security.JwtUtil;
import com.proyecto.servicios.service.sf.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
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

    @Value("${jwt.expiration}")
    private Long expiration;

    @Override
    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BadCredentialsException("Credenciales de autenticación inválidas."));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new UnauthorizedAccessException("El usuario se encuentra inactivo. Contacte al administrador del sistema.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales de autenticación inválidas.");
        }

        String token = jwtUtil.generateToken(usuario.getCorreo());
        return new AuthResponse(token, "Bearer", expiration);
    }
}
