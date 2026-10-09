package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DomicilioRequest {
    @NotBlank
    private String calle;

    @NotBlank
    private String numeroExterior;

    private String numeroInterior;

    @NotBlank
    private String colonia;

    @NotBlank
    private String municipio;

    @NotNull
    private Long estadoId;

    @NotBlank
    @Pattern(regexp = "^\\d{5}$")
    private String codigoPostal;
}
