package edu.bbte.replate.dto.outgoing;

public record ImageOutDto(
        Long id,
        String imageName,
        String filePath,
        String imageMimeType
) {
}
