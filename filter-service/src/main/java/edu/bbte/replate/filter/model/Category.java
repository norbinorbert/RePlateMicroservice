package edu.bbte.replate.filter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Entity
@ToString(callSuper = true, exclude = "subcategories")
public class Category extends BaseEntity {
    @Column(nullable = false, unique = true, length = 63)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    @OrderBy("name ASC")
    private List<Category> subcategories;
}
