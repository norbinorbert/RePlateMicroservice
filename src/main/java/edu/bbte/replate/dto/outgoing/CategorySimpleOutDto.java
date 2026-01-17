package edu.bbte.replate.dto.outgoing;

public record CategorySimpleOutDto(
        Long id,
        String name,
        Long parentCategoryId
) {
}
