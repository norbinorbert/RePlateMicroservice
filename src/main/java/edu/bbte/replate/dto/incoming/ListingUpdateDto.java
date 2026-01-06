package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ListingUpdateDto(
        @NotNull
        Long id,

        @NotBlank
        @Size(min = 1, max = 100)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull
        @PositiveOrZero
        double price,

        @NotNull
        long cityId,

        @Size(max = 100)
        String locationDetails,

        @NotNull
        long categoryId
) {
}
