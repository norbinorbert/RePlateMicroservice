package edu.bbte.replate.listing.controller;

import edu.bbte.replate.listing.mapper.ImageMapper;
import edu.bbte.replate.listing.model.Image;
import edu.bbte.replate.listing.service.ImageService;
import edu.bbte.replate.shared.dto.outgoing.ImageDownloadDto;
import edu.bbte.replate.shared.dto.outgoing.ImageOutDto;
import edu.bbte.replate.shared.dto.outgoing.SimpleMessageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/listings/{listingId}/images")
@Slf4j
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@listingSecurity.isOwner(#listingId)")
    public ResponseEntity<SimpleMessageResponseDto> handleUploadImage(
            @PathVariable long listingId,
            @RequestPart("file") MultipartFile file
    ) {
        log.info("Handling POST /listing/{}/images request.", listingId);

        Image createdImage = imageService.upload(listingId, file);

        URI createdUri = URI.create("/listings/" + listingId + "/images/" + createdImage.getId());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(createdUri);

        var responseBody = new SimpleMessageResponseDto("New image uploaded successfully.");

        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{imageId}/download")
    public ResponseEntity<Resource> handleDownloadImage(
            @PathVariable long listingId,
            @PathVariable long imageId
    ) {
        ImageDownloadDto downloadDto = imageService.download(listingId, imageId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadDto.mimeType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + downloadDto.fileName() + "\""
                )
                .body(downloadDto.resource());
    }

    @GetMapping
    public ResponseEntity<List<ImageOutDto>> handleGetAllImagesByListingId(@PathVariable long listingId) {
        log.info("Handling GET /listings/{}/images request.", listingId);

        List<ImageOutDto> outDtos = imageService.findImagesByListingId(listingId)
                .stream()
                .map(imageMapper::imageToOutDto)
                .toList();

        return ResponseEntity.ok(outDtos);
    }

    @GetMapping("/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ImageOutDto> handleGetImageOfListingById(
            @PathVariable long listingId,
            @PathVariable long imageId
    ) {
        log.info("Handling GET /listings/{}/images/{} request.", listingId, imageId);

        // Exceptions handled in service
        Image image = imageService.findByListingIdAndImageId(listingId, imageId);

        return ResponseEntity.ok(imageMapper.imageToOutDto(image));
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@listingSecurity.isOwner(#listingId)")
    public ResponseEntity<SimpleMessageResponseDto> handleDeleteImageOfListingById(
            @PathVariable long listingId,
            @PathVariable long imageId
    ) {
        log.info("Handling DELETE /listings/{}/images/{} request.", listingId, imageId);

        // Exceptions handled in service
        imageService.delete(listingId, imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
