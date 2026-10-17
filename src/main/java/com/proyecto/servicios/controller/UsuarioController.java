package com.proyecto.servicios.controller;

import com.proyecto.servicios.model.common.ApiResponse;
import com.proyecto.servicios.model.sf.UsuarioCreateRequest;
import com.proyecto.servicios.model.sf.UsuarioIdRequest;
import com.proyecto.servicios.model.sf.UsuarioResponse;
import com.proyecto.servicios.model.sf.UsuarioSearchRequest;
import com.proyecto.servicios.model.sf.UsuarioStatusRequest;
import com.proyecto.servicios.model.sf.UsuarioUpdateRequest;
import com.proyecto.servicios.service.sf.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para la administración y consulta de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear nuevo usuario con validaciones de entrada")
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearUsuario(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse nuevoUsuario = usuarioService.crearUsuario(request);
        return new ResponseEntity<>(ApiResponse.success(nuevoUsuario, "Usuario creado exitosamente."), HttpStatus.CREATED);
    }

    @PostMapping("/buscar")
    @Operation(summary = "Búsqueda dinámica de usuarios combinando filtros en un único endpoint")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> buscarUsuarios(@Valid @RequestBody(required = false) UsuarioSearchRequest request) {
        List<UsuarioResponse> resultados = usuarioService.buscarUsuarios(request);
        return ResponseEntity.ok(ApiResponse.success(resultados, "Consulta realizada con éxito. Total encontrados: " + resultados.size()));
    }

    @PostMapping("/detalle")
    @Operation(summary = "Consultar detalle de un usuario por ID mediante Request Body")
    public ResponseEntity<ApiResponse<UsuarioResponse>> consultarPorId(@Valid @RequestBody UsuarioIdRequest request) {
        UsuarioResponse usuario = usuarioService.consultarPorId(request);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Usuario obtenido exitosamente."));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Consultar perfil del usuario autenticado actual")
    public ResponseEntity<ApiResponse<UsuarioResponse>> consultarPerfil(Authentication authentication) {
        String correo = authentication != null ? authentication.getName() : null;
        UsuarioResponse usuario = usuarioService.consultarPerfil(correo);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Perfil consultado exitosamente."));
    }

    @PutMapping
    @Operation(summary = "Actualizar datos de usuario (impide modificación directa de campos controlados por el servidor)")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizarUsuario(
            @Valid @RequestBody UsuarioUpdateRequest request,
            Authentication authentication) {
        String correo = authentication != null ? authentication.getName() : null;
        UsuarioResponse usuarioActualizado = usuarioService.actualizarUsuario(request, correo);
        return ResponseEntity.ok(ApiResponse.success(usuarioActualizado, "Usuario actualizado exitosamente."));
    }

    @PatchMapping("/estatus")
    @Operation(summary = "Cambiar estatus de usuario (activo/inactivo)")
    public ResponseEntity<ApiResponse<UsuarioResponse>> cambiarEstatus(
            @Valid @RequestBody UsuarioStatusRequest request,
            Authentication authentication) {
        String correo = authentication != null ? authentication.getName() : null;
        UsuarioResponse usuarioActualizado = usuarioService.cambiarEstatus(request, correo);
        return ResponseEntity.ok(ApiResponse.success(usuarioActualizado, "Estatus de usuario actualizado exitosamente."));
    }

    @DeleteMapping
    @Operation(summary = "Baja lógica de usuario mediante Request Body")
    public ResponseEntity<ApiResponse<Void>> bajaLogica(
            @Valid @RequestBody UsuarioIdRequest request,
            Authentication authentication) {
        String correo = authentication != null ? authentication.getName() : null;
        usuarioService.bajaLogica(request, correo);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario dado de baja exitosamente."));
    }
}
