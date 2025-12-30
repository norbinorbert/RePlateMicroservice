package edu.bbte.replate.service;

import edu.bbte.replate.model.Category;

import java.util.List;

public interface CategoryService {
    Category findById(Long id);

    Category findByName(String name);

    List<Category> findTopLevelCategories();

    List<Category> findSubcategories(String parentCategoryName);
}
