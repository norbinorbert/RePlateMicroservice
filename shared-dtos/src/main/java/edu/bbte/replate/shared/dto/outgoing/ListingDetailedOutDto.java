package edu.bbte.replate.shared.dto.outgoing;

import java.sql.Timestamp;
import java.util.List;

public record ListingDetailedOutDto(
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
        Long ownerId,
        List<ImageOutDto> images
) {
}
