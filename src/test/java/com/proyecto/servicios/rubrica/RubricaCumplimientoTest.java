package com.proyecto.servicios.rubrica;

import com.proyecto.servicios.controller.AuthController;
import com.proyecto.servicios.controller.UsuarioController;
import com.proyecto.servicios.entity.sf.Cliente;
import com.proyecto.servicios.entity.sf.Cuenta;
import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.exceptions.BusinessException;
import com.proyecto.servicios.exceptions.DuplicateResourceException;
import com.proyecto.servicios.exceptions.GlobalExceptionHandler;
import com.proyecto.servicios.exceptions.ResourceNotFoundException;
import com.proyecto.servicios.exceptions.UnauthorizedAccessException;
import com.proyecto.servicios.exceptions.ValidationException;
import com.proyecto.servicios.model.common.ApiResponse;
import com.proyecto.servicios.model.sf.UsuarioCreateRequest;
import com.proyecto.servicios.model.sf.UsuarioIdRequest;
import com.proyecto.servicios.model.sf.UsuarioResponse;
import com.proyecto.servicios.repositorys.sf.UsuarioRepository;
import com.proyecto.servicios.service.sf.UsuarioService;
import com.proyecto.servicios.service.sf.Impl.UsuarioServiceImpl;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de CUMPLIMIENTO DE RÚBRICA.
 * Verifican por reflexión y por análisis de código fuente que la arquitectura
 * y las buenas prácticas exigidas por la rúbrica se cumplen realmente.
 */
@DisplayName("Rúbrica - Verificación estructural del proyecto")
class RubricaCumplimientoTest {

    private static final String PAQUETE_BASE = "com.proyecto.servicios";

    private static final Class<? extends Annotation>[] ANOTACIONES_MAPEO = new Class[]{
            RequestMapping.class, GetMapping.class, PostMapping.class,
            PutMapping.class, PatchMapping.class, DeleteMapping.class
    };

    // ---------------------------------------------------------------------
    // Utilidades de escaneo
    // ---------------------------------------------------------------------

    private Set<Class<?>> clasesAnotadasCon(Class<? extends Annotation> anotacion) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(anotacion));
        Set<Class<?>> resultado = new LinkedHashSet<>();
        for (BeanDefinition bd : provider.findCandidateComponents(PAQUETE_BASE)) {
            try {
                resultado.add(Class.forName(bd.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("No se pudo cargar la clase " + bd.getBeanClassName(), e);
            }
        }
        return resultado;
    }

    private boolean esMetodoDeEndpoint(Method m) {
        for (Class<? extends Annotation> anotacion : ANOTACIONES_MAPEO) {
            if (m.isAnnotationPresent(anotacion)) {
                return true;
            }
        }
        return false;
    }

    private Path rutaFuente(Class<?> clase) {
        return Paths.get("src", "main", "java", clase.getName().replace('.', '/') + ".java");
    }

    private String leerFuente(Class<?> clase) throws IOException {
        Path ruta = rutaFuente(clase);
        assertTrue(Files.exists(ruta), "No se encontró el código fuente de " + clase.getSimpleName() + " en " + ruta.toAbsolutePath());
        return Files.readString(ruta);
    }

    // ---------------------------------------------------------------------
    // 1. Arquitectura por capas: Controller / Service / Repository / Entity
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("1. Existen controladores REST anotados con @RestController")
    void controladoresAnotadosConRestController() {
        Set<Class<?>> controladores = clasesAnotadasCon(RestController.class);
        assertFalse(controladores.isEmpty(), "Debe existir al menos un @RestController");
        assertTrue(controladores.contains(UsuarioController.class), "Debe existir UsuarioController");
        assertTrue(controladores.contains(AuthController.class), "Debe existir AuthController");
    }

    @Test
    @DisplayName("2. Existen entidades JPA anotadas con @Entity")
    void entidadesAnotadasConEntity() {
        Set<Class<?>> entidades = clasesAnotadasCon(Entity.class);
        assertFalse(entidades.isEmpty(), "Debe existir al menos una entidad @Entity");
        assertTrue(entidades.contains(Usuario.class), "Debe existir la entidad Usuario");
        assertTrue(entidades.contains(Cliente.class), "Debe existir la entidad Cliente");
        assertTrue(entidades.contains(Cuenta.class), "Debe existir la entidad Cuenta");
    }

    @Test
    @DisplayName("3. Existe un repositorio que extiende JpaRepository")
    void repositoriosExtiendenJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(UsuarioRepository.class),
                "UsuarioRepository debe extender JpaRepository");
    }

    @Test
    @DisplayName("4. Existe interfaz de servicio e implementación anotada con @Service")
    void servicioTieneInterfazEImplementacion() {
        assertTrue(UsuarioService.class.isInterface(), "UsuarioService debe ser una interfaz");
        assertTrue(UsuarioService.class.isAssignableFrom(UsuarioServiceImpl.class),
                "UsuarioServiceImpl debe implementar UsuarioService");
        assertNotNull(UsuarioServiceImpl.class.getAnnotation(Service.class),
                "UsuarioServiceImpl debe estar anotado con @Service");
    }

    // ---------------------------------------------------------------------
    // 2. DTOs de Request / Response (no exponer entidades)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("5. Los DTOs de Request usan Bean Validation")
    void dtoRequestUsanBeanValidation() throws NoSuchFieldException {
        assertTrue(UsuarioCreateRequest.class.getDeclaredField("correo").isAnnotationPresent(Email.class),
                "UsuarioCreateRequest.correo debe tener @Email");
        assertTrue(UsuarioIdRequest.class.getDeclaredField("id").isAnnotationPresent(NotNull.class)
                        && UsuarioIdRequest.class.getDeclaredField("id").isAnnotationPresent(Positive.class),
                "UsuarioIdRequest.id debe tener @NotNull y @Positive");
    }

    @Test
    @DisplayName("6. Los DTOs de Response NO son entidades JPA")
    void dtoResponseNoEsEntidad() {
        assertFalse(UsuarioResponse.class.isAnnotationPresent(Entity.class),
                "UsuarioResponse no debe ser una entidad @Entity");
        for (Field f : UsuarioResponse.class.getDeclaredFields()) {
            assertFalse(f.isAnnotationPresent(jakarta.persistence.Column.class),
                    "UsuarioResponse no debe tener columnas JPA: " + f.getName());
        }
    }

    // ---------------------------------------------------------------------
    // 3. Reglas de controladores: @Valid, sin @PathVariable/@RequestParam, sin try-catch
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("7. Todos los @RequestBody de los controladores están validados con @Valid")
    void parametrosRequestBodyTienenValid() {
        for (Class<?> controlador : clasesAnotadasCon(RestController.class)) {
            for (Method metodo : controlador.getDeclaredMethods()) {
                if (!esMetodoDeEndpoint(metodo)) {
                    continue;
                }
                for (Parameter p : metodo.getParameters()) {
                    if (p.isAnnotationPresent(RequestBody.class)) {
                        assertTrue(p.isAnnotationPresent(Valid.class),
                                controlador.getSimpleName() + "." + metodo.getName()
                                        + " usa @RequestBody sin @Valid");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("8. PROHIBIDO: los controladores no usan @PathVariable ni @RequestParam")
    void controladoresSinPathVariableNiRequestParam() {
        for (Class<?> controlador : clasesAnotadasCon(RestController.class)) {
            for (Method metodo : controlador.getDeclaredMethods()) {
                for (Parameter p : metodo.getParameters()) {
                    assertFalse(p.isAnnotationPresent(PathVariable.class),
                            controlador.getSimpleName() + "." + metodo.getName() + " usa @PathVariable (prohibido)");
                    assertFalse(p.isAnnotationPresent(RequestParam.class),
                            controlador.getSimpleName() + "." + metodo.getName() + " usa @RequestParam (prohibido)");
                }
            }
        }
    }

    @Test
    @DisplayName("9. PROHIBIDO: los controladores no contienen try-catch")
    void controladoresSinTryCatch() throws IOException {
        for (Class<?> controlador : clasesAnotadasCon(RestController.class)) {
            String fuente = leerFuente(controlador);
            assertFalse(fuente.matches("(?s).*\\btry\\s*\\{.*"),
                    controlador.getSimpleName() + " contiene bloques try (prohibido)");
            assertFalse(fuente.matches("(?s).*\\bcatch\\s*\\(.*"),
                    controlador.getSimpleName() + " contiene bloques catch (prohibido)");
        }
    }

    @Test
    @DisplayName("10. Los controladores devuelven ResponseEntity y NUNCA entidades")
    void controladoresDevuelvenResponseEntity() {
        Set<Class<?>> entidades = clasesAnotadasCon(Entity.class);
        for (Class<?> controlador : clasesAnotadasCon(RestController.class)) {
            for (Method metodo : controlador.getDeclaredMethods()) {
                if (!esMetodoDeEndpoint(metodo)) {
                    continue;
                }
                assertTrue(ResponseEntity.class.isAssignableFrom(metodo.getReturnType()),
                        controlador.getSimpleName() + "." + metodo.getName() + " debe devolver ResponseEntity");
                assertFalse(entidades.contains(metodo.getReturnType()),
                        controlador.getSimpleName() + "." + metodo.getName() + " devuelve una entidad (prohibido)");
            }
        }
    }

    // ---------------------------------------------------------------------
    // 4. Búsqueda dinámica en un único endpoint con @RequestBody
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("11. La búsqueda dinámica se expone en un único endpoint con @RequestBody")
    void busquedaDinamicaUsaRequestBody() throws NoSuchMethodException {
        Method buscar = UsuarioController.class.getMethod("buscarUsuarios", com.proyecto.servicios.model.sf.UsuarioSearchRequest.class);
        assertTrue(buscar.isAnnotationPresent(PostMapping.class), "La búsqueda debe exponerse con @PostMapping");
        boolean tieneRequestBody = false;
        for (Parameter p : buscar.getParameters()) {
            if (p.isAnnotationPresent(RequestBody.class)) {
                tieneRequestBody = true;
            }
        }
        assertTrue(tieneRequestBody, "La búsqueda dinámica debe recibir los filtros por @RequestBody");
    }

    // ---------------------------------------------------------------------
    // 5. Manejo centralizado de excepciones
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("12. Existe @RestControllerAdvice con @ExceptionHandler")
    void manejadorGlobalDeExcepciones() {
        assertNotNull(GlobalExceptionHandler.class.getAnnotation(RestControllerAdvice.class),
                "GlobalExceptionHandler debe estar anotado con @RestControllerAdvice");

        List<Class<?>> manejadas = new ArrayList<>();
        for (Method m : GlobalExceptionHandler.class.getDeclaredMethods()) {
            ExceptionHandler anotacion = m.getAnnotation(ExceptionHandler.class);
            if (anotacion != null) {
                manejadas.addAll(Arrays.asList(anotacion.value()));
            }
        }
        assertTrue(manejadas.contains(ResourceNotFoundException.class), "Debe manejar ResourceNotFoundException");
        assertTrue(manejadas.contains(DuplicateResourceException.class), "Debe manejar DuplicateResourceException");
        assertTrue(manejadas.contains(ValidationException.class), "Debe manejar ValidationException");
        assertTrue(manejadas.contains(BusinessException.class), "Debe manejar BusinessException");
        assertTrue(manejadas.contains(UnauthorizedAccessException.class), "Debe manejar UnauthorizedAccessException");
        assertTrue(manejadas.contains(Exception.class), "Debe manejar Exception de forma genérica");
    }

    @Test
    @DisplayName("13. Las excepciones propias extienden RuntimeException")
    void excepcionesPropiasExtiendenRuntimeException() {
        Class<?>[] propias = {BusinessException.class, DuplicateResourceException.class,
                ResourceNotFoundException.class, UnauthorizedAccessException.class, ValidationException.class};
        for (Class<?> excepcion : propias) {
            assertTrue(RuntimeException.class.isAssignableFrom(excepcion),
                    excepcion.getSimpleName() + " debe extender RuntimeException");
            assertTrue(excepcion.getPackageName().equals("com.proyecto.servicios.exceptions"),
                    excepcion.getSimpleName() + " debe residir en el paquete de excepciones");
        }
    }

    // ---------------------------------------------------------------------
    // 6. Estructura uniforme de respuesta
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("14. ApiResponse define la estructura uniforme de respuesta")
    void apiResponseTieneEstructuraEstandar() {
        Set<String> campos = new LinkedHashSet<>();
        for (Field f : ApiResponse.class.getDeclaredFields()) {
            campos.add(f.getName());
        }
        assertTrue(campos.containsAll(Arrays.asList("success", "message", "data", "timestamp", "errors")),
                "ApiResponse debe contener success, message, data, timestamp y errors. Campos actuales: " + campos);
    }
}
