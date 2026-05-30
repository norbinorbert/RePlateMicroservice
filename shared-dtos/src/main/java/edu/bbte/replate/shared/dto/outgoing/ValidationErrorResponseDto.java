package edu.bbte.replate.shared.dto.outgoing;

import java.util.Map;

public record ValidationErrorResponseDto(
        Map<String, String> errors
) {
}
