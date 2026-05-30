package edu.bbte.replate.filter.mapper;

import edu.bbte.replate.filter.model.County;
import edu.bbte.replate.shared.dto.outgoing.CountySimpleOutDto;
import edu.bbte.replate.shared.dto.outgoing.CountyWithParentCountryOutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CountryMapper.class)
public interface CountyMapper {
    CountyWithParentCountryOutDto toWithParentOutDto(County county);

    CountySimpleOutDto toSimpleOutDto(County county);
}
