package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record CityCreateDto(
        @NotBlank(message = "Name cannot be empty.")
        String name,

        @NonNull
        Long countyId
) {
}
