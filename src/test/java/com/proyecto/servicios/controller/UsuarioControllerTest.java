package com.proyecto.servicios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.servicios.exceptions.BusinessException;
import com.proyecto.servicios.exceptions.DuplicateResourceException;
import com.proyecto.servicios.exceptions.GlobalExceptionHandler;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.UnauthorizedAccessException;
import com.proyecto.servicios.model.sf.*;
import com.proyecto.servicios.service.sf.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testCrearUsuarioValidoRetorna201() throws Exception {
        UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                .correo("valido@empresa.com")
                .password("Password123!")
                .rol("ROLE_USER")
                .build();

        UsuarioResponse responseDto = UsuarioResponse.builder()
                .id(1L)
                .correo("valido@empresa.com")
                .rol("ROLE_USER")
                .activo(true)
                .build();

        when(usuarioService.crearUsuario(any(UsuarioCreateRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.correo").value("valido@empresa.com"));
    }

    @Test
    void testCrearUsuarioDatosInvalidosRetorna400ConErrores() throws Exception {
        UsuarioCreateRequest requestInvalido = UsuarioCreateRequest.builder()
                .correo("no-es-correo")
                .password("corta") // Falla longitud y patrón
                .build();

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.correo").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void testBuscarUsuariosRetorna200() throws Exception {
        UsuarioSearchRequest request = UsuarioSearchRequest.builder()
                .correo("carlos")
                .build();

        UsuarioResponse responseDto = UsuarioResponse.builder()
                .id(1L)
                .correo("carlos@empresa.com")
                .activo(true)
                .build();

        when(usuarioService.buscarUsuarios(any())).thenReturn(List.of(responseDto));

        mockMvc.perform(post("/usuarios/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].correo").value("carlos@empresa.com"));
    }

    @Test
    void testConsultarPorIdMedianteRequestBodyRetorna200() throws Exception {
        UsuarioIdRequest request = new UsuarioIdRequest(1L);

        UsuarioResponse responseDto = UsuarioResponse.builder()
                .id(1L)
                .correo("carlos@empresa.com")
                .activo(true)
                .build();

        when(usuarioService.consultarPorId(any(UsuarioIdRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/usuarios/detalle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testConsultarPorIdInvalidoRetorna400() throws Exception {
        // ID nulo o negativo
        UsuarioIdRequest requestInvalido = new UsuarioIdRequest(-5L);

        mockMvc.perform(post("/usuarios/detalle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.id").exists());
    }

    @Test
    void testBajaLogicaMedianteRequestBodyRetorna200() throws Exception {
        UsuarioIdRequest request = new UsuarioIdRequest(1L);

        mockMvc.perform(delete("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ------------------------------------------------------------------
    // Códigos HTTP adicionales exigidos por la rúbrica
    // ------------------------------------------------------------------

    @Test
    void testCrearUsuarioDuplicadoRetorna409() throws Exception {
        UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                .correo("repetido@empresa.com")
                .password("Password123!")
                .build();

        when(usuarioService.crearUsuario(any(UsuarioCreateRequest.class)))
                .thenThrow(new DuplicateResourceException("El correo ya está registrado."));

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testConsultarPorIdNoEncontradoRetorna404() throws Exception {
        UsuarioIdRequest request = new UsuarioIdRequest(999L);

        when(usuarioService.consultarPorId(any(UsuarioIdRequest.class)))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado con ID: 999"));

        mockMvc.perform(post("/usuarios/detalle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testActualizarSinPermisoRetorna403() throws Exception {
        UsuarioUpdateRequest request = UsuarioUpdateRequest.builder()
                .id(1L)
                .correo("otro@empresa.com")
                .build();

        when(usuarioService.actualizarUsuario(any(UsuarioUpdateRequest.class), any()))
                .thenThrow(new UnauthorizedAccessException("No tiene autorización para modificar este usuario."));

        mockMvc.perform(put("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testReglaDeNegocioEstatusRetorna422() throws Exception {
        UsuarioStatusRequest request = UsuarioStatusRequest.builder()
                .id(1L)
                .activo(false)
                .build();

        when(usuarioService.cambiarEstatus(any(UsuarioStatusRequest.class), any()))
                .thenThrow(new BusinessException("No es posible desactivar este usuario."));

        mockMvc.perform(patch("/usuarios/estatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testJsonMalformadoRetorna400() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"correo\": \"incompleto\" "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testMetodoNoSoportadoRetorna405() throws Exception {
        mockMvc.perform(get("/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testExcepcionInesperadaRetorna500SinDetallesInternos() throws Exception {
        UsuarioSearchRequest request = UsuarioSearchRequest.builder().correo("x").build();

        when(usuarioService.buscarUsuarios(any()))
                .thenThrow(new RuntimeException("SQL: SELECT * FROM usuarios WHERE password='secreto'"));

        mockMvc.perform(post("/usuarios/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Ha ocurrido un error interno al procesar su solicitud. Por favor contacte al administrador."));
    }
}
