package edu.bbte.replate.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class Country extends BaseEntity {
    @Column(nullable = false, unique = true, length = 63)
    private String name;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<County> counties;
}

