package edu.bbte.replate.listing.controller;

import edu.bbte.replate.listing.client.AuthServiceClient;
import edu.bbte.replate.listing.mapper.ImageMapper;
import edu.bbte.replate.listing.model.Image;
import edu.bbte.replate.listing.service.ImageService;
import edu.bbte.replate.listing.util.ListingSecurity;
import edu.bbte.replate.shared.dto.internal.TokenValidationResponseDto;
import edu.bbte.replate.shared.dto.outgoing.ImageDownloadDto;
import edu.bbte.replate.shared.dto.outgoing.ImageOutDto;
import edu.bbte.replate.shared.dto.outgoing.SimpleMessageResponseDto;
import edu.bbte.replate.shared.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private ListingSecurity listingSecurity;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SimpleMessageResponseDto> handleUploadImage(
            @PathVariable long listingId,
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) {
        log.info("Handling POST /listing/{}/images request.", listingId);

        // Validate user token
        TokenValidationResponseDto userValidation = validateAndGetUser(authHeader);
        if (!userValidation.valid() || userValidation.id() == null) {
            throw new BadRequestException("Invalid or missing authentication token.");
        }

        // Check ownership
        if (!listingSecurity.isOwnerByUserId(listingId, userValidation.id())) {
            throw new BadRequestException("User is not authorized to upload images for this listing.");
        }

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
    public ResponseEntity<SimpleMessageResponseDto> handleDeleteImageOfListingById(
            @PathVariable long listingId,
            @PathVariable long imageId,
            @RequestHeader("Authorization") String authHeader
    ) {
        log.info("Handling DELETE /listings/{}/images/{} request.", listingId, imageId);

        // Validate user token
        TokenValidationResponseDto userValidation = validateAndGetUser(authHeader);
        if (!userValidation.valid() || userValidation.id() == null) {
            throw new BadRequestException("Invalid or missing authentication token.");
        }

        // Check ownership
        if (!listingSecurity.isOwnerByUserId(listingId, userValidation.id())) {
            throw new BadRequestException("User is not authorized to upload images for this listing.");
        }

        // Exceptions handled in service
        imageService.delete(listingId, imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private TokenValidationResponseDto validateAndGetUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header.");
            return new TokenValidationResponseDto(null, null, null, false);
        }

        String token = authHeader.substring(7);
        return authServiceClient.validateToken(token);
    }
}
