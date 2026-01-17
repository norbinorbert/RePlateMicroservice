package edu.bbte.replate.controller;

import edu.bbte.replate.dto.outgoing.CategorySimpleOutDto;
import edu.bbte.replate.dto.outgoing.CategoryTreeOutDto;
import edu.bbte.replate.dto.outgoing.SimpleMessageResponseDto;
import edu.bbte.replate.exception.ResourceNotFoundException;
import edu.bbte.replate.mapper.CategoryMapper;
import edu.bbte.replate.model.Category;
import edu.bbte.replate.service.CategoryService;
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
    public ResponseEntity<CategoryTreeOutDto> handleGetCategory(@PathVariable long id) {
        log.info("Handling GET /categories/{} request.", id);

        Category category = categoryService.findById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category with id " + id + " not found.");
        }

        CategoryTreeOutDto categoryTree = categoryMapper.categoryToCategoryTreeOutDto(category);

        return ResponseEntity.ok(categoryTree);
    }
}
