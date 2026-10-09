package com.proyecto.servicios.controller;

import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/filtro")
    public ResponseEntity<List<Usuario>> filtroUsuarios() {
        // En una implementación real, aquí se usarían RequestParams para filtrar.
        // Como el requerimiento no especifica los filtros, devolveremos todos.
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PutMapping("/agregar")
    public ResponseEntity<Usuario> agregarUsuario(@RequestBody Usuario usuario) {
        // Este endpoint es adicional según los requerimientos.
        // Usualmente el usuario se crea al registrar el cliente,
        // pero este método permite actualizar o agregar uno manualmente si es necesario.
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }
}
