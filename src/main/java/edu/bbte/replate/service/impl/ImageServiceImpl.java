package edu.bbte.replate.service.impl;

import edu.bbte.replate.dto.outgoing.ImageDownloadDto;
import edu.bbte.replate.exception.InternalServerErrorException;
import edu.bbte.replate.exception.ResourceNotFoundException;
import edu.bbte.replate.model.Image;
import edu.bbte.replate.model.Listing;
import edu.bbte.replate.repository.ImageRepository;
import edu.bbte.replate.service.ImageService;
import edu.bbte.replate.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ImageServiceImpl implements ImageService  {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ListingService listingService;

    @Value("${app.images.upload-dir}")
    private Path uploadDir;

    @Override
    public Image findById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    @Override
    public Image findByListingIdAndImageId(Long listingId, Long imageId) {
        Listing listing = getListingOrElseThrow(listingId);

        return listing.getImages().stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No image with id " + imageId + " was found."));
    }

    @Override
    public Image findByName(String imageName) {
        return imageRepository.findImageByImageName(imageName);
    }

    @Override
    public List<Image> findImagesByListingId(Long listingId) {
        return getListingOrElseThrow(listingId).getImages();
    }

    @Override
    public Image upload(Long listingId, MultipartFile file) {
        // It would be a bad idea to move this down right before adding the new image to the listing, since:
        // - We check for non-existent listing ASAP
        // - If the listing truly did not exist, the image would still get uploaded.
        @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
        Listing listing = getListingOrElseThrow(listingId);

        validateImage(file);

        String storedFileName = generateFileName(file);
        Path targetPath = uploadDir.resolve(storedFileName);

        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            log.error("Failed to store image because: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to store image.");
        }

        Image image = new Image();
        image.setImageName(storedFileName);
        image.setFilePath(targetPath.toString());
        image.setImageMimeType(file.getContentType());

        listing.getImages().add(image);
        listingService.update(listing);
        return image;
    }

    @Override
    public ImageDownloadDto download(Long listingId, Long imageId) {
        Listing listing = getListingOrElseThrow(listingId);

        Image image = listing.getImages()
                .stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No image with id " + imageId + " was found."));

        Path path = Path.of(image.getFilePath());

        if (!Files.exists(path)) {
            log.error("Image file for image with id {} is missing.", imageId);
            throw new InternalServerErrorException("Image file is missing");
        }

        try {
            Resource resource = new UrlResource(path.toUri());
            return new ImageDownloadDto(
                    resource,
                    image.getImageName(),
                    image.getImageMimeType()
            );
        } catch (MalformedURLException e) {
            log.error("Invalid image path: {}", e.getMessage());
            throw new InternalServerErrorException("Invalid image path.");
        }
    }

    @Override
    public void delete(Long listingId, Long imageId) {
        Listing listing = getListingOrElseThrow(listingId);

        Image image = listing.getImages()
                .stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No image with id " + imageId + " was found."));

        deleteFile(image.getFilePath());
        listing.getImages().remove(image);
        listingService.update(listing);
    }

    private Listing getListingOrElseThrow(Long listingId) {
        Listing listing = listingService.findById(listingId);
        if (listing == null) {
            throw new ResourceNotFoundException("No listing with id " + listingId + " was found.");
        }
        return listing;
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Provided image file cannot be empty.");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image type files are allowed.");
        }
    }

    // Never take the original file name for security reasons -> generate one instead
    private String generateFileName(MultipartFile file) {
        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.indexOf('.')))
                .orElse("");

        return UUID.randomUUID() + ext;
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            log.error("Failed to delete image, because: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to delete image.");
        }
    }
}
