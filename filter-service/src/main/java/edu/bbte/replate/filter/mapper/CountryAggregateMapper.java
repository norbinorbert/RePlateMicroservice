package edu.bbte.replate.filter.mapper;

import edu.bbte.replate.filter.model.Country;
import edu.bbte.replate.shared.dto.outgoing.CountryWithChildCountiesOutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CountyMapper.class)
public interface CountryAggregateMapper {
    CountryWithChildCountiesOutDto toWithChildrenDto(Country country);
}
