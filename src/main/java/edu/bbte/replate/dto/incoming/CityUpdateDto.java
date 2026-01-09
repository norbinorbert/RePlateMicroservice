package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

public record CityUpdateDto(
        @NonNull
        Long id,

        @NotBlank
        String name,

        @NonNull
        Long countyId
) {
}
