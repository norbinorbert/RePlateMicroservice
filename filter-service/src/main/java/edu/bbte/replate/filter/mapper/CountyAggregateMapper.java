package edu.bbte.replate.filter.mapper;

import edu.bbte.replate.filter.model.County;
import edu.bbte.replate.shared.dto.outgoing.CountyWithChildCitiesOutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CityMapper.class)
public interface CountyAggregateMapper {
    CountyWithChildCitiesOutDto toWithChildrenDto(County county);
}
