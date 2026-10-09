package com.proyecto.servicios.model.sf;

import lombok.Data;

@Data
public class DomicilioResponse {
    private String calle;
    private String numeroExterior;
    private String numeroInterior;
    private String colonia;
    private String municipio;
    private String estado;
    private String codigoPostal;
    private String pais;
}
