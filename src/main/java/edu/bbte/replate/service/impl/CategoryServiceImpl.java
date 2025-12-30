package edu.bbte.replate.service.impl;

import edu.bbte.replate.model.Category;
import edu.bbte.replate.repository.CategoryRepository;
import edu.bbte.replate.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
