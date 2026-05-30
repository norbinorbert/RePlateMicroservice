package edu.bbte.replate.shared.dto.outgoing;

import java.sql.Timestamp;
import java.util.List;

public record ListingDetailedOutDto(
        Long id,
        String title,
        String description,
        Double price,
        Timestamp datePosted,
        CityWithParentCountyOutDto city,
        String locationDetails,
        CategorySimpleOutDto category,
        Long ownerId,
        List<ImageOutDto> images
) {
}
