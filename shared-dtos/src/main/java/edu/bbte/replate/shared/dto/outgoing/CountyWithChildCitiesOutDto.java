package edu.bbte.replate.shared.dto.outgoing;

import java.util.List;

public record CountyWithChildCitiesOutDto(
        Long id,
        String name,
        List<CitySimpleOutDto> cities
) {
}
