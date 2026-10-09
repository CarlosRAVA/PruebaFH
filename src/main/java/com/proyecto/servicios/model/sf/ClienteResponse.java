package com.proyecto.servicios.model.sf;

import com.proyecto.servicios.entity.sf.Cliente;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClienteResponse {
    private Long id;
    private String nombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaNacimiento;
    private String curp;
    private String rfc;
    private String sexo;
    private String paisNacionalidad;
    private String estadoCivil;
    private String correoElectronico;
    private Long telefonoMovil;
    private Long telefonoAlternativo;
    private String ocupacion;
    private String empresa;
    private BigDecimal ingresoMensual;
    private Boolean activo;
    private DomicilioResponse domicilio;

    public ClienteResponse(Cliente c) {
        this.id = c.getId();
        this.nombre = c.getNombre();
        this.segundoNombre = c.getSegundoNombre();
        this.apellidoPaterno = c.getApellidoPaterno();
        this.apellidoMaterno = c.getApellidoMaterno();
        this.fechaNacimiento = c.getFechaNacimiento();
        this.curp = c.getCurp();
        this.rfc = c.getRfc();
        this.sexo = c.getSexo();
        this.paisNacionalidad = c.getPaisNacionalidad() != null ? c.getPaisNacionalidad().getNombre() : null;
        this.estadoCivil = c.getEstadoCivil();
        this.correoElectronico = c.getCorreoElectronico();
        this.telefonoMovil = c.getTelefonoMovil();
        this.telefonoAlternativo = c.getTelefonoAlternativo();
        this.ocupacion = c.getOcupacion();
        this.empresa = c.getEmpresa();
        this.ingresoMensual = c.getIngresoMensual();
        this.activo = c.getActivo();
        if (c.getDomicilio() != null) {
            this.domicilio = new DomicilioResponse();
            this.domicilio.setCalle(c.getDomicilio().getCalle());
            this.domicilio.setNumeroExterior(c.getDomicilio().getNumeroExterior());
            this.domicilio.setNumeroInterior(c.getDomicilio().getNumeroInterior());
            this.domicilio.setColonia(c.getDomicilio().getColonia());
            this.domicilio.setMunicipio(c.getDomicilio().getMunicipio());
            this.domicilio.setEstado(c.getDomicilio().getEstado() != null ? c.getDomicilio().getEstado().getNombre() : null);
            this.domicilio.setCodigoPostal(c.getDomicilio().getCodigoPostal() != null ? c.getDomicilio().getCodigoPostal().getCodigo() : null);
            this.domicilio.setPais(c.getDomicilio().getEstado() != null && c.getDomicilio().getEstado().getPais() != null ? c.getDomicilio().getEstado().getPais().getNombre() : null);
        }
    }
}
