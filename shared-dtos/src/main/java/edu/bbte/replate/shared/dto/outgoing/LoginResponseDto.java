package edu.bbte.replate.shared.dto.outgoing;

public record LoginResponseDto(
        String message,
        String token,
        Long userId
) {
}
