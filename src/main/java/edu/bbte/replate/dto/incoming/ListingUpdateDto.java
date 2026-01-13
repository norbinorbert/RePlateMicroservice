package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ListingUpdateDto(
        @NotNull
        Long id,

        @NotBlank(message = "Title cannot be empty.")
        @Size(min = 1, max = 100)
        String title,

        @Size(max = 1000, message = "Description must have a maximum of 1000 characters.")
        String description,

        @NotNull
        @PositiveOrZero(message = "Price must be greater or equal than 0.")
        double price,

        @NotNull
        long cityId,

        @Size(max = 100, message = "LocationDetails must have a maximum of 100 characters.")
        String locationDetails,

        @NotNull
        long categoryId
) {
}
