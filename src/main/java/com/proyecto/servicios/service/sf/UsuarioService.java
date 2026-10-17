package com.proyecto.servicios.service.sf;

import com.proyecto.servicios.model.sf.UsuarioCreateRequest;
import com.proyecto.servicios.model.sf.UsuarioIdRequest;
import com.proyecto.servicios.model.sf.UsuarioResponse;
import com.proyecto.servicios.model.sf.UsuarioSearchRequest;
import com.proyecto.servicios.model.sf.UsuarioStatusRequest;
import com.proyecto.servicios.model.sf.UsuarioUpdateRequest;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse crearUsuario(UsuarioCreateRequest request);

    List<UsuarioResponse> buscarUsuarios(UsuarioSearchRequest request);

    UsuarioResponse consultarPorId(UsuarioIdRequest request);

    UsuarioResponse consultarPerfil(String correoAutenticado);

    UsuarioResponse actualizarUsuario(UsuarioUpdateRequest request, String correoAutenticado);

    UsuarioResponse cambiarEstatus(UsuarioStatusRequest request, String correoAutenticado);

    void bajaLogica(UsuarioIdRequest request, String correoAutenticado);
}
