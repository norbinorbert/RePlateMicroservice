package edu.bbte.replate.listing.repository;

import edu.bbte.replate.listing.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findImageByImageName(String imageName);
}
