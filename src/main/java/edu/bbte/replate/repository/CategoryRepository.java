package edu.bbte.replate.repository;

import edu.bbte.replate.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryByName(String name);

    List<Category> findCategoriesByParentCategoryIsNull();
}
