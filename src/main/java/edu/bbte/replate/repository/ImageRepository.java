package edu.bbte.replate.repository;

import edu.bbte.replate.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
