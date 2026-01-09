package edu.bbte.replate.dto.outgoing;

public record CountyOutDto(
        Long id,
        String name,
        CountryOutDto countryOutDto
) {
}
