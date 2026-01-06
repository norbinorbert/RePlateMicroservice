package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank
        @Size(min = 1, max=31)
        String username,

        @NotBlank
        @Size(min = 1, max = 254)
        String email,

        @NotBlank
        @Size(min = 1, max = 64)
        String password,

        @NotBlank
        @Size(min = 1, max = 64)
        String repeatPassword,

        @NotBlank
        @Size(max = 13)
        String phoneNumber
) {}
