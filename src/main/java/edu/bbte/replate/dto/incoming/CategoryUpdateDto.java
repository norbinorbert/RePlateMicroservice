package edu.bbte.replate.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

public record CategoryUpdateDto(
        @NonNull
        Long id,

        @NotBlank(message = "Name cannot be empty.")
        @Size(max = 63, message = "Name must have a maximum of 63 characters.")
        String name,

        Long parentCategoryId
) {
}
