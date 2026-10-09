package com.proyecto.servicios.security;

import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo.");
        }

        return new User(usuario.getCorreo(), usuario.getPassword(), new ArrayList<>());
    }
}
