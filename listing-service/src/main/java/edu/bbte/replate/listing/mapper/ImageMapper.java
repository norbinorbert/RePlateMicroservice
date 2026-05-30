package edu.bbte.replate.listing.mapper;

import edu.bbte.replate.listing.model.Image;
import edu.bbte.replate.shared.dto.outgoing.ImageOutDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageOutDto imageToOutDto(Image image);
}
