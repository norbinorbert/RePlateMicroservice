package edu.bbte.replate.dto.outgoing;

public record CityOutDto(
        Long id,
        String name,
        CountyOutDto countyOutDto
) {
}
