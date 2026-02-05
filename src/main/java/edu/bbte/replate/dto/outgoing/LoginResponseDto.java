package edu.bbte.replate.dto.outgoing;

public record LoginResponseDto(
        String message,
        String token,
        Long userId
) {
}
