package com.alfonso.jwtsecurity.dto;

import com.alfonso.jwtsecurity.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50, message = "El nombre completo debe tener entre 3 a 50 caracteres")
    private String fullname;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 10, message = "El nombre de usuario debe tener entre 3 a 10 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private Role role;
}
