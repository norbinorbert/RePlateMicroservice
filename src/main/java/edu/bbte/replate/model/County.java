package edu.bbte.replate.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class County extends BaseEntity {
    @Column(nullable = false, length = 63)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "county", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<City> cities;
}
