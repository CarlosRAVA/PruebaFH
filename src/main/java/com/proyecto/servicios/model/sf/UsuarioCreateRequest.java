package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class UsuarioCreateRequest {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico es inválido")
    @Size(max = 100, message = "El correo electrónico no puede exceder 100 caracteres")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$",
        message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un caracter especial (@#$%^&+=!._-)"
    )
    private String password;

    private Long clienteId;

    @Pattern(regexp = "^(ROLE_USER|ROLE_ADMIN|ROLE_CLIENTE)?$", message = "El rol debe ser ROLE_USER, ROLE_ADMIN o ROLE_CLIENTE")
    private String rol;

    public UsuarioCreateRequest() {
    }

    @Builder
    public UsuarioCreateRequest(String correo, String password, Long clienteId, String rol) {
        this.correo = correo;
        this.password = password;
        this.clienteId = clienteId;
        this.rol = rol;
    }
}
