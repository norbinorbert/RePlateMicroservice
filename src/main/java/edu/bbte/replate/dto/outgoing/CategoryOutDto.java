package edu.bbte.replate.dto.outgoing;

public record CategoryOutDto(
        Long id,
        String name,
        Long parentCategoryId
) {
}
