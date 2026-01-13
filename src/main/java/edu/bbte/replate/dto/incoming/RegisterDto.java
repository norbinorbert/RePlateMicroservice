package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank(message = "Username cannot be empty.")
        @Size(min = 1, max=31, message = "Username must have between 1 and 31 characters.")
        String username,

        @NotBlank(message = "Email cannot be empty.")
        @Size(min = 1, max = 254, message = "Email must have a maximum of 254 characters.")
        String email,

        @NotBlank(message = "Password cannot be empty.")
        @Size(min = 1, max = 64, message = "Password must have a maximum of 64 characters.")
        String password,

        @NotBlank(message = "Repeat password cannot be empty.")
        @Size(min = 1, max = 64, message = "Repeat password must have a maximum of 64 characters.")
        String repeatPassword,

        @NotBlank(message = "Phone number cannot be empty.")
        @Size(max = 13, message = "Phone number must have a maximum of 13 digits.")
        String phoneNumber
) {}
