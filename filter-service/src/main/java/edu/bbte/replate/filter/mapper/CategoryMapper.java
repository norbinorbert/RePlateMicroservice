package edu.bbte.replate.filter.mapper;

import edu.bbte.replate.filter.model.Category;
import edu.bbte.replate.shared.dto.incoming.CategoryCreateDto;
import edu.bbte.replate.shared.dto.outgoing.CategoryParentOutDto;
import edu.bbte.replate.shared.dto.outgoing.CategorySimpleOutDto;
import edu.bbte.replate.shared.dto.outgoing.CategoryTreeOutDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    Category createDtoToCategory(CategoryCreateDto dto);

    @Mapping(target = "parentCategoryId", source = "parentCategory.id")
    CategorySimpleOutDto categoryToSimpleOutDto(Category category);

    // Define default methods, since MapStruct can fall into infinite loops on parent-child relationships
    default CategoryTreeOutDto categoryToCategoryTreeOutDto(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryTreeOutDto(
                category.getId(),
                category.getName(),
                categoryToCategoryParentOutDto(category.getParentCategory()),
                category.getSubcategories() == null ? List.of() : category.getSubcategories()
                        .stream()
                        .map(this::categoryToCategoryTreeOutDto)
                        .toList()
        );
    }

    default CategoryParentOutDto categoryToCategoryParentOutDto(Category parent) {
        if (parent == null) {
            return null;
        }

        return new CategoryParentOutDto(
                parent.getId(),
                parent.getName()
        );
    }
}
