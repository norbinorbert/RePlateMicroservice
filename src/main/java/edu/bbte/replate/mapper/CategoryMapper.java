package edu.bbte.replate.mapper;

import edu.bbte.replate.dto.incoming.CategoryCreateDto;
import edu.bbte.replate.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category createDtoToCategory(CategoryCreateDto dto);
}
