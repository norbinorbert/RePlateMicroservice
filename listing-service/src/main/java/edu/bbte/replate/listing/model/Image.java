package edu.bbte.replate.listing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString(callSuper = true)
public class Image extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String imageName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String imageMimeType;
}
