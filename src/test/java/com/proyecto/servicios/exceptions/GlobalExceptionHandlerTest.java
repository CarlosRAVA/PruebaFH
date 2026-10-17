package com.proyecto.servicios.exceptions;

import com.proyecto.servicios.model.common.ApiResponse;
import com.proyecto.servicios.model.sf.UsuarioCreateRequest;
import com.proyecto.servicios.model.sf.UsuarioIdRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Manejo centralizado de excepciones - códigos HTTP")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private Validator validator;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Recurso no encontrado -> 404")
    void recursoNoEncontrado() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleResourceNotFoundException(new ResourceNotFoundException("x"));
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertFalse(r.getBody().isSuccess());
    }

    @Test
    @DisplayName("Recurso duplicado -> 409")
    void recursoDuplicado() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleDuplicateResourceException(new DuplicateResourceException("x"));
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
    }

    @Test
    @DisplayName("Validación de negocio -> 400")
    void validacionNegocio() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleValidationException(new ValidationException("x"));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
    }

    @Test
    @DisplayName("Regla de negocio -> 422")
    void reglaDeNegocio() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleBusinessException(new BusinessException("x"));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, r.getStatusCode());
    }

    @Test
    @DisplayName("Acceso no autorizado (propio) -> 403")
    void accesoNoAutorizado() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleUnauthorizedAccessException(new UnauthorizedAccessException("x"));
        assertEquals(HttpStatus.FORBIDDEN, r.getStatusCode());
    }

    @Test
    @DisplayName("Acceso denegado (Spring Security) -> 403")
    void accesoDenegado() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleAccessDeniedException(new AccessDeniedException("x"));
        assertEquals(HttpStatus.FORBIDDEN, r.getStatusCode());
    }

    @Test
    @DisplayName("Credenciales inválidas -> 401")
    void credencialesInvalidas() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleBadCredentialsException(new BadCredentialsException("x"));
        assertEquals(HttpStatus.UNAUTHORIZED, r.getStatusCode());
    }

    @Test
    @DisplayName("JSON malformado -> 400")
    void jsonMalformado() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("malformed", (HttpInputMessage) null));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
    }

    @Test
    @DisplayName("Violación de integridad de datos -> 409 (mensaje genérico)")
    void integridadDeDatos() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleDataIntegrityViolationException(
                new DataIntegrityViolationException("duplicate key value violates unique constraint \"usuarios_correo_key\""));
        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertFalse(r.getBody().getMessage().contains("unique constraint"),
                "No debe filtrar detalles internos de la base de datos");
    }

    @Test
    @DisplayName("Método HTTP no soportado -> 405")
    void metodoNoSoportado() {
        ResponseEntity<ApiResponse<Void>> r = handler.handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("GET"));
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, r.getStatusCode());
    }

    @Test
    @DisplayName("Validación de @Valid en @RequestBody -> 400 con mapa de errores")
    void validacionDeCuerpo() throws Exception {
        Method metodo = DummyController.class.getMethod("crear", UsuarioCreateRequest.class);
        MethodParameter parametro = new MethodParameter(metodo, 0);
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new UsuarioCreateRequest(), "usuarioCreateRequest");
        bindingResult.addError(new FieldError("usuarioCreateRequest", "correo", "El formato del correo electrónico es inválido"));

        ResponseEntity<ApiResponse<Void>> r = handler.handleMethodArgumentNotValidException(
                new MethodArgumentNotValidException(parametro, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertNotNull(r.getBody().getErrors());
        assertTrue(r.getBody().getErrors().containsKey("correo"));
    }

    @Test
    @DisplayName("Violación de restricciones (ConstraintViolation) -> 400")
    void violacionDeRestricciones() {
        Set<ConstraintViolation<UsuarioIdRequest>> violaciones = validator.validate(new UsuarioIdRequest(-1L));
        assertFalse(violaciones.isEmpty(), "El DTO inválido debe generar violaciones");

        ResponseEntity<ApiResponse<Void>> r = handler.handleConstraintViolationException(
                new ConstraintViolationException(violaciones));
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertNotNull(r.getBody().getErrors());
    }

    @Test
    @DisplayName("Excepción inesperada -> 500 SIN filtrar trazas ni detalles internos")
    void excepcionInesperadaNoFiltraDetalles() {
        RuntimeException interna = new RuntimeException(
                "SQL: SELECT * FROM usuarios WHERE password = 'admin-secreto' -- filtración");

        ResponseEntity<ApiResponse<Void>> r = handler.handleGlobalException(interna);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        String mensaje = r.getBody().getMessage();
        assertFalse(mensaje.contains("SQL"), "No debe exponer SQL");
        assertFalse(mensaje.contains("SELECT"), "No debe exponer consultas");
        assertFalse(mensaje.contains("admin-secreto"), "No debe exponer credenciales/datos internos");
        assertEquals("Ha ocurrido un error interno al procesar su solicitud. Por favor contacte al administrador.", mensaje);
    }

    /** Controlador de apoyo para construir un MethodArgumentNotValidException real. */
    static class DummyController {
        public Object crear(UsuarioCreateRequest request) {
            return request;
        }
    }
}
