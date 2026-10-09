package com.proyecto.servicios.service.sf;

import com.proyecto.servicios.model.sf.ClienteRequest;
import com.proyecto.servicios.model.sf.ClienteResponse;
import com.proyecto.servicios.model.sf.ClienteUpdateRequest;

import java.util.List;

public interface ClienteService {
    ClienteResponse registrarCliente(ClienteRequest request);
    List<ClienteResponse> consultarTodos();
    ClienteResponse consultarPorId(Long id);
    List<ClienteResponse> buscarPorNombre(String nombre);
    List<ClienteResponse> buscarPorApellidoPaterno(String apellidoPaterno);
    List<ClienteResponse> buscarPorApellidoMaterno(String apellidoMaterno);
    ClienteResponse consultarPorCurp(String curp);
    ClienteResponse consultarPorRfc(String rfc);
    ClienteResponse consultarPorCorreo(String correo);
    List<ClienteResponse> consultarActivos();
    List<ClienteResponse> consultarPorRangoFechas(java.time.LocalDateTime start, java.time.LocalDateTime end);
    ClienteResponse actualizarParcial(Long id, ClienteUpdateRequest request);
    void bajaLogica(Long id);
}
