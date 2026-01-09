package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

public record CountryUpdateDto(
        @NonNull
        Long id,

        @NotBlank
        @Size(max = 63)
        String name
) {
}
