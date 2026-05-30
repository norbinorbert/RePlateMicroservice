package edu.bbte.replate.shared.dto.outgoing;

public record CountyWithParentCountryOutDto(
        Long id,
        String name,
        CountrySimpleOutDto country
) {
}
