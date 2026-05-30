package edu.bbte.replate.shared.dto.internal;

import java.util.List;

/**
 * Internal DTO for inter-service token validation
 * Returned by Auth Service to other services
 */
public record TokenValidationResponse(
        boolean valid,
        String username,
        List<String> roles,
        Long userId
) {
}

