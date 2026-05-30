package edu.bbte.replate.filter.mapper;

import edu.bbte.replate.filter.model.City;
import edu.bbte.replate.shared.dto.outgoing.CitySimpleOutDto;
import edu.bbte.replate.shared.dto.outgoing.CityWithParentCountyOutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CountyMapper.class)
public interface CityMapper {
    CityWithParentCountyOutDto toWithParentOutDto(City city);

    CitySimpleOutDto toSimpleOutDto(City city);
}
