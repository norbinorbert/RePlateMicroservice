package edu.bbte.replate.shared.dto.internal;

public record TokenValidationResponseDto(
        Long id,
        String username,
        String email,
        boolean valid
) {
}

