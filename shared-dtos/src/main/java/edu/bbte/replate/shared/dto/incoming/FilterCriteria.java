package edu.bbte.replate.shared.dto.incoming;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class FilterCriteria {
    @Length(max = 100)
    private String title;

    @PositiveOrZero
    private Double minPrice;

    @PositiveOrZero
    private Double maxPrice;

    private Long countryId;

    private Long countyId;

    private Long cityId;

    private Long categoryId;
}
