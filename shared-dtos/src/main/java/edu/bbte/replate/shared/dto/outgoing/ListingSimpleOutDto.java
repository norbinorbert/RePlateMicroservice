package edu.bbte.replate.shared.dto.outgoing;

import java.sql.Timestamp;

public record ListingSimpleOutDto(
        Long id,
        String title,
        String description,
        Double price,
        Timestamp datePosted,
        CityWithParentCountyOutDto city,
        String locationDetails,
        CategorySimpleOutDto category,
        Long ownerId
) {
}
