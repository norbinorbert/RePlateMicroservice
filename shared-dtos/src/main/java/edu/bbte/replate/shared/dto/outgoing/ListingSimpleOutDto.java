package edu.bbte.replate.shared.dto.outgoing;

import java.sql.Timestamp;

public record ListingSimpleOutDto(
        Long id,
        String title,
        String description,
        Double price,
        Timestamp datePosted,
        Long cityId,
        Long countyId,
        Long countryId,
        String locationDetails,
        Long categoryId,
        Long ownerId
) {
}
