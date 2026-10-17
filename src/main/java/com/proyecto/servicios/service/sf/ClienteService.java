package com.proyecto.servicios.service.sf;

import com.proyecto.servicios.model.sf.ClienteIdRequest;
import com.proyecto.servicios.model.sf.ClienteRequest;
import com.proyecto.servicios.model.sf.ClienteResponse;
import com.proyecto.servicios.model.sf.ClienteSearchRequest;
import com.proyecto.servicios.model.sf.ClienteUpdateRequest;

import java.util.List;

public interface ClienteService {
    ClienteResponse registrarCliente(ClienteRequest request);
    List<ClienteResponse> buscarClientes(ClienteSearchRequest request);
    ClienteResponse consultarPorId(ClienteIdRequest request);
    ClienteResponse consultarPorId(Long id);
    ClienteResponse actualizarCliente(ClienteUpdateRequest request);
    ClienteResponse actualizarParcial(Long id, ClienteUpdateRequest request);
    void bajaLogica(ClienteIdRequest request);
    void bajaLogica(Long id);

    List<ClienteResponse> consultarTodos();
    List<ClienteResponse> buscarPorNombre(String nombre);
    List<ClienteResponse> buscarPorApellidoPaterno(String apellidoPaterno);
    List<ClienteResponse> buscarPorApellidoMaterno(String apellidoMaterno);
    ClienteResponse consultarPorCurp(String curp);
    ClienteResponse consultarPorRfc(String rfc);
    ClienteResponse consultarPorCorreo(String correo);
    List<ClienteResponse> consultarActivos();
    List<ClienteResponse> consultarPorRangoFechas(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
