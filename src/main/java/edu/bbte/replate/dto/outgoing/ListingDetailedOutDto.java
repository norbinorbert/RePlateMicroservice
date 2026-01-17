package edu.bbte.replate.dto.outgoing;

import java.sql.Timestamp;
import java.util.List;

public record ListingDetailedOutDto(
        Long id,
        String title,
        String description,
        Double price,
        Timestamp datePosted,
        CityOutDto city,
        String locationDetails,
        CategorySimpleOutDto category,
        Long ownerId,
        List<ImageOutDto> images
) {
}
