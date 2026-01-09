package edu.bbte.replate.mapper;

import edu.bbte.replate.dto.outgoing.CountyOutDto;
import edu.bbte.replate.model.County;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CountryMapper.class)
public interface CountyMapper {
    CountyOutDto toOutDto(County county);
}
