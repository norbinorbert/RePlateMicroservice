package edu.bbte.replate.dto.outgoing;

import java.sql.Timestamp;

public record ListingSimpleOutDto(
        Long id,
        String title,
        String description,
        Double price,
        Timestamp datePosted,
        CityOutDto city,
        String locationDetails,
        CategoryOutDto category,
        Long ownerId
) {
}
