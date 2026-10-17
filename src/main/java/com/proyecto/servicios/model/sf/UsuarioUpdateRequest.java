package com.proyecto.servicios.model.sf;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class UsuarioUpdateRequest {

    @NotNull(message = "El ID del usuario es obligatorio para la actualización")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long id;

    @Email(message = "El formato del correo electrónico es inválido")
    @Size(max = 100, message = "El correo electrónico no puede exceder 100 caracteres")
    private String correo;

    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$",
        message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un caracter especial (@#$%^&+=!._-)"
    )
    private String password;

    @Pattern(regexp = "^(ROLE_USER|ROLE_ADMIN|ROLE_CLIENTE)?$", message = "El rol debe ser ROLE_USER, ROLE_ADMIN o ROLE_CLIENTE")
    private String rol;

    private Long clienteId;

    public UsuarioUpdateRequest() {
    }

    @Builder
    public UsuarioUpdateRequest(Long id, String correo, String password, String rol, Long clienteId) {
        this.id = id;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.clienteId = clienteId;
    }
}
