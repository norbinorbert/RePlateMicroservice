package edu.bbte.replate.filter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Entity
@ToString(callSuper = true, exclude = "counties")
public class Country extends BaseEntity {
    @Column(nullable = false, unique = true, length = 63)
    private String name;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<County> counties;
}

