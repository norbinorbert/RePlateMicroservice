package edu.bbte.replate.filter.service;


import edu.bbte.replate.filter.model.Category;

import java.util.List;

public interface CategoryService {
    Category findById(Long id);

    Category findByName(String name);

    List<Category> findTopLevelCategories();

    List<Category> findSubcategories(String parentCategoryName);

    List<Category> getParentCategoryChain(Long id);
}
