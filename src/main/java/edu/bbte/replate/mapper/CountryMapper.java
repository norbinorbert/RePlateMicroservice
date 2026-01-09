package edu.bbte.replate.mapper;

import edu.bbte.replate.dto.outgoing.CountryOutDto;
import edu.bbte.replate.model.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryOutDto toOutDto(Country country);
}
