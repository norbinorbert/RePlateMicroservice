package edu.bbte.replate.filter.mapper;

import edu.bbte.replate.filter.model.Country;
import edu.bbte.replate.shared.dto.outgoing.CountrySimpleOutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountrySimpleOutDto toSimpleOutDto(Country country);
}
