package edu.bbte.replate.service;

import edu.bbte.replate.dto.outgoing.ImageDownloadDto;
import edu.bbte.replate.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    Image findById(Long id);

    Image findByListingIdAndImageId(Long listingId, Long imageId);

    Image findByName(String imageName);

    List<Image> findImagesByListingId(Long listingId);

    Image upload(Long listingId, MultipartFile file);

    ImageDownloadDto download(Long listingId, Long imageId);

    void delete(Long listingId, Long imageId);
}
