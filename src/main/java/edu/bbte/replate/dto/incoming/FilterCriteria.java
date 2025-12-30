package edu.bbte.replate.dto.incoming;

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

    @Length(max = 63)
    private String countryName;

    @Length(max = 63)
    private String countyName;

    @Length(max = 255)
    private String cityName;

    @Length(max = 63)
    private String categoryName;
}
