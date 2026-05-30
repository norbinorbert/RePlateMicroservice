package edu.bbte.replate.filter.service.impl;

import edu.bbte.replate.filter.model.Category;
import edu.bbte.replate.filter.repository.CategoryRepository;
import edu.bbte.replate.filter.service.CategoryService;
import edu.bbte.replate.shared.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category findById(Long id) {
        log.info("Requested category with id: {}", id);
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Category findByName(String name) {
        log.info("Requested category with name: {}", name);
        return categoryRepository.findCategoryByName(name);
    }

    @Override
    public List<Category> findTopLevelCategories() {
        log.info("Requested top-level categories");
        return categoryRepository.findCategoriesByParentCategoryIsNull();
    }

    @Override
    public List<Category> findSubcategories(String parentCategoryName) {
        log.info("Requested subcategories of category: {}", parentCategoryName);
        Category parent = categoryRepository.findCategoryByName(parentCategoryName);
        return parent != null ? parent.getSubcategories() : null;
    }

    @Override
    public List<Category> getParentCategoryChain(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        /*
         * Throw here, because if we return an empty list, we cannot tell if the category has no parents
         * or just doesn't exist at all
         * */
        if (category == null) {
            throw new ResourceNotFoundException(
                    "No category with id " + id + " was found, so it cannot have parent categories.");
        }

        List<Category> parents = new ArrayList<>();
        Category current = category.getParentCategory();

        while (current != null) {
            parents.add(current);
            current = current.getParentCategory();
        }

        // So it points from top-level to the leaf
        Collections.reverse(parents);
        return parents;
    }
}
