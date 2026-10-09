package com.proyecto.servicios.service.sf;

import com.proyecto.servicios.model.sf.AuthRequest;
import com.proyecto.servicios.model.sf.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
