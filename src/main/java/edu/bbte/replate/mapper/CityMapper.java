package edu.bbte.replate.mapper;

import edu.bbte.replate.dto.outgoing.CityOutDto;
import edu.bbte.replate.model.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CountyMapper.class)
public interface CityMapper {
    CityOutDto toOutDto(City city);
}
