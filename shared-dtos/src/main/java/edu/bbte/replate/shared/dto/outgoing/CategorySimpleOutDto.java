package edu.bbte.replate.shared.dto.outgoing;

public record CategorySimpleOutDto(
        Long id,
        String name,
        Long parentCategoryId
) {
}
