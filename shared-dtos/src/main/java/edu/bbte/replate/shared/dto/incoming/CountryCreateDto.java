package edu.bbte.replate.shared.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CountryCreateDto(
        @NotBlank(message = "Name cannot be empty.")
        @Size(max = 63, message = "Name must have a maximum of 63 characters.")
        String name
) {
}
