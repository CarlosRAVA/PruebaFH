package com.proyecto.servicios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.servicios.exceptions.GlobalExceptionHandler;
import com.proyecto.servicios.model.sf.AuthRequest;
import com.proyecto.servicios.model.sf.AuthResponse;
import com.proyecto.servicios.service.sf.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - login y credenciales")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Login correcto -> 200 con token")
    void loginCorrecto() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setCorreo("carlos@empresa.com");
        request.setPassword("Password123!");

        when(authService.login(any(AuthRequest.class)))
                .thenReturn(new AuthResponse("jwt.token.falso", "Bearer", 3600000L));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt.token.falso"))
                .andExpect(jsonPath("$.data.tipoToken").value("Bearer"));
    }

    @Test
    @DisplayName("Credenciales inválidas -> 401")
    void credencialesInvalidas() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setCorreo("carlos@empresa.com");
        request.setPassword("incorrecta");

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Credenciales de autenticación inválidas."));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Datos de login inválidos -> 400")
    void datosInvalidos() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setCorreo("no-es-correo");
        request.setPassword("");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.correo").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
