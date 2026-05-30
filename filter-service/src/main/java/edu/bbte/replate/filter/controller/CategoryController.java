package edu.bbte.replate.filter.controller;

import edu.bbte.replate.filter.mapper.CategoryMapper;
import edu.bbte.replate.filter.model.Category;
import edu.bbte.replate.filter.service.CategoryService;
import edu.bbte.replate.shared.dto.outgoing.CategorySimpleOutDto;
import edu.bbte.replate.shared.dto.outgoing.CategoryTreeOutDto;
import edu.bbte.replate.shared.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/top-level")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CategorySimpleOutDto>> handleGetTopLevel() {
        log.info("Handling GET /categories/top-level request.");

        List<Category> topLevelCategories = categoryService.findTopLevelCategories();

        List<CategorySimpleOutDto> outDtos = topLevelCategories
                .stream()
                .map(categoryMapper::categoryToSimpleOutDto)
                .toList();

        return ResponseEntity.ok(outDtos);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryTreeOutDto> handleGetById(@PathVariable long id) {
        log.info("Handling GET /categories/{} request.", id);

        Category category = categoryService.findById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category with id " + id + " not found.");
        }

        CategoryTreeOutDto categoryTree = categoryMapper.categoryToCategoryTreeOutDto(category);

        return ResponseEntity.ok(categoryTree);
    }

    @GetMapping("/{id}/subcategories")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CategorySimpleOutDto>> handleGetSubcategoriesById(@PathVariable long id) {
        log.info("Handling GET /categories/{}/subcategories request.", id);

        Category category = categoryService.findById(id);
        if (category == null) {
            throw new ResourceNotFoundException(
                    "Category with id " + id + " not found, so it cannot have subcategories.");
        }

        List<Category> subcategories = categoryService.findSubcategories(category.getName());

        List<CategorySimpleOutDto> outDtos = subcategories
                .stream()
                .map(categoryMapper::categoryToSimpleOutDto)
                .toList();

        return ResponseEntity.ok(outDtos);
    }

    @GetMapping("/{id}/parent-categories")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CategorySimpleOutDto>> handleGetParentsById(@PathVariable long id) {
        log.info("Handling GET /categories/{}/parent-categories request.", id);

        // This method call already throws ResourceNotFoundException if no category with id exists.
        List<Category> parents = categoryService.getParentCategoryChain(id);

        List<CategorySimpleOutDto> outDtos = parents
                .stream()
                .map(categoryMapper::categoryToSimpleOutDto)
                .toList();

        return ResponseEntity.ok(outDtos);
    }
}
