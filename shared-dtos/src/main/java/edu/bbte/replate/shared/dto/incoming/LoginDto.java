package edu.bbte.replate.shared.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto(
        @NotBlank
        @Size(min = 1, max = 31)
        String username,

        @NotBlank
        @Size(min = 1, max = 64)
        String password
) {}
