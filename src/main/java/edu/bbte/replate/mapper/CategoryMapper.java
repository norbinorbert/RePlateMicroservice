package edu.bbte.replate.mapper;

import edu.bbte.replate.dto.incoming.CategoryCreateDto;
import edu.bbte.replate.dto.outgoing.CategorySimpleOutDto;
import edu.bbte.replate.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    Category createDtoToCategory(CategoryCreateDto dto);

    @Mapping(target = "parentCategoryId", source = "parentCategory.id")
    CategorySimpleOutDto categoryToSimpleOutDto(Category category);
}
