package com.gritlab.model;

import com.gritlab.validator.NotDuplicatedEmail;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Builder
@Data
@AllArgsConstructor
public class UserDTO {

    @JsonIgnore
    private String id;

    @NotNull(message = "Name is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Name cannot be empty or contain only spaces")
    @Size(min = 1, max = 50, message = "Name length must be between 1 and 50 characters")
    private String name;

    @NotNull(message = "Email is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Email cannot be empty or contain only spaces")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    @Email(message = "Email must be in valid format")
    @NotDuplicatedEmail(message = "Email already taken, please use another one")
    private String email;

    @NotNull(message = "Password is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Password cannot be empty or contain only spaces")
    @Size(min = 6, max = 50, message = "Password length must be between 6 and 50 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, " +
                    "one digit and one special character (@$!%*?&)")
    @JsonIgnore
    private String password;

    @NotNull(message = "Role is required")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Role cannot be empty or contain only spaces")
    @Pattern(regexp = "^(?i)(admin|user)$", message = "Role must be either 'ADMIN' or 'USER' (case-insensitive)")
    private String role;
}
