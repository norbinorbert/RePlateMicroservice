package edu.bbte.replate.shared.dto.outgoing;

public record CityWithParentCountyOutDto(
        Long id,
        String name,
        CountyWithParentCountryOutDto county
) {
}
