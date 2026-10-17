package com.proyecto.servicios.service;

import com.proyecto.servicios.entity.sf.Cliente;
import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.exceptions.DuplicateResourceException;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.UnauthorizedAccessException;
import com.proyecto.servicios.model.sf.*;
import com.proyecto.servicios.repositorys.sf.ClienteRepository;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import com.proyecto.servicios.service.sf.Impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(1L)
                .correo("carlos@example.com")
                .password("encoded_pass")
                .rol("ROLE_USER")
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    void testCrearUsuarioExitoso() {
        UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                .correo("nuevo@example.com")
                .password("Password123!")
                .rol("ROLE_USER")
                .build();

        when(usuarioRepository.findByCorreo("nuevo@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        UsuarioResponse response = usuarioService.crearUsuario(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuarioCorreoDuplicadoLanzaExcepcion() {
        UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                .correo("carlos@example.com")
                .password("Password123!")
                .build();

        when(usuarioRepository.findByCorreo("carlos@example.com")).thenReturn(Optional.of(usuarioMock));

        assertThrows(DuplicateResourceException.class, () -> usuarioService.crearUsuario(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testConsultarPorIdUsuarioNoEncontradoLanzaExcepcion() {
        UsuarioIdRequest request = new UsuarioIdRequest(99L);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.consultarPorId(request));
    }

    @Test
    void testActualizarUsuarioMismoUsuarioExitoso() {
        UsuarioUpdateRequest request = UsuarioUpdateRequest.builder()
                .id(1L)
                .correo("nuevo_correo@example.com")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.findByCorreo("nuevo_correo@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        UsuarioResponse response = usuarioService.actualizarUsuario(request, "carlos@example.com");

        assertNotNull(response);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuarioOtroUsuarioSinAdminLanzaUnauthorizedAccessException() {
        UsuarioUpdateRequest request = UsuarioUpdateRequest.builder()
                .id(1L)
                .correo("hacker@example.com")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.findByCorreo("otro@example.com")).thenReturn(Optional.of(
                Usuario.builder().id(2L).correo("otro@example.com").rol("ROLE_USER").build()
        ));

        assertThrows(UnauthorizedAccessException.class, () -> usuarioService.actualizarUsuario(request, "otro@example.com"));
    }

    @Test
    void testBuscarUsuariosFiltrosDinamicos() {
        UsuarioSearchRequest request = UsuarioSearchRequest.builder()
                .activo(true)
                .build();

        when(usuarioRepository.findAll(any(Specification.class))).thenReturn(List.of(usuarioMock));

        List<UsuarioResponse> resultados = usuarioService.buscarUsuarios(request);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("carlos@example.com", resultados.get(0).getCorreo());
    }

    @Test
    void testBajaLogicaExitoso() {
        UsuarioIdRequest request = new UsuarioIdRequest(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        usuarioService.bajaLogica(request, "carlos@example.com");

        assertFalse(usuarioMock.getActivo());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }
}
