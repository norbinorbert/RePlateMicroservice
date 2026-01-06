package edu.bbte.replate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString(callSuper = true, exclude = "images")
public class Listing extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @PositiveOrZero
    private Double price;

    @Column(nullable = false, updatable = false)
    private Timestamp datePosted;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(length = 100)
    private String locationDetails;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "listing_id")
    private List<Image> images;

    @PrePersist
    private void onCreate() {
        datePosted = new Timestamp(System.currentTimeMillis());
        images = new ArrayList<>();
    }
}
