package edu.bbte.replate.listing.controller;

import edu.bbte.replate.listing.client.AuthServiceClient;
import edu.bbte.replate.listing.client.FilterServiceClient;
import edu.bbte.replate.listing.mapper.ListingMapper;
import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.listing.service.ListingService;
import edu.bbte.replate.shared.dto.incoming.FilterCriteria;
import edu.bbte.replate.shared.dto.incoming.ListingCreateDto;
import edu.bbte.replate.shared.dto.incoming.ListingUpdateDto;
import edu.bbte.replate.shared.dto.outgoing.CityWithParentCountyOutDto;
import edu.bbte.replate.shared.dto.outgoing.ListingDetailedOutDto;
import edu.bbte.replate.shared.dto.outgoing.ListingSimpleOutDto;
import edu.bbte.replate.shared.dto.outgoing.SimpleMessageResponseDto;
import edu.bbte.replate.shared.exception.BadRequestException;
import edu.bbte.replate.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/listings")
@Slf4j
public class ListingController {
    @Autowired
    private ListingService listingService;

    @Autowired
    private FilterServiceClient filterServiceClient;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private ListingMapper listingMapper;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SimpleMessageResponseDto> handlePost(
            @RequestBody @Valid ListingCreateDto dto,
            @AuthenticationPrincipal UserDetails principal
    ) {
        log.info("Handling POST /listings request.");

        CityWithParentCountyOutDto city = filterServiceClient.getCityById(dto.cityId());
        // Validate city exists in location service
        if (city == null) {
            throw new BadRequestException("No city with id " + dto.cityId() + " exists.");
        }

        // Validate category exists in location service
        if (filterServiceClient.getCategoryById(dto.categoryId()) == null) {
            throw new BadRequestException("No category with id " + dto.categoryId() + " exists.");
        }

        // Get user ID from auth service
        Long userId = authServiceClient.getUserIdByUsername(principal.getUsername());
        if (userId == null) {
            throw new BadRequestException("User not found: " + principal.getUsername());
        }

        Listing listing = listingMapper.createDtoToListing(dto);
        listing.setCityId(city.id());
        listing.setCountyId(city.county().id());
        listing.setCountryId(city.county().country().id());
        listing.setCategoryId(dto.categoryId());
        listing.setOwnerId(userId);

        Listing createdListing = listingService.create(listing);

        // Send the created entity's access path in the Location header
        URI createdUri = URI.create("/listings/" + createdListing.getId());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(createdUri);

        var responseBody = new SimpleMessageResponseDto("New listing created successfully.");

        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListingDetailedOutDto> handleGetById(@PathVariable long id) {
        log.info("Handling GET /listings/{} request.", id);

        Listing listing = listingService.findById(id);

        if (listing == null) {
            throw new ResourceNotFoundException("Listing with id " + id + " not found.");
        }

        ListingDetailedOutDto outDto = listingMapper.listingToDetailedOutDto(listing);

        return ResponseEntity.ok(outDto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ListingSimpleOutDto>> handleGetAll(
            @ModelAttribute @Valid FilterCriteria filterCriteria,
            @PageableDefault(
                    size = 20,
                    sort = "datePosted",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        log.info("Handling GET /listings request.");

        Page<Listing> listings;

        if (!isAnyFilterCriterionSet(filterCriteria)) {
            listings = listingService.findAll(pageable);
        } else {
            listings = listingService.findByFilters(filterCriteria, pageable);
        }

        // Return number of page and total number of pages in header
        HttpHeaders headers = new HttpHeaders();
        headers.add("page", String.valueOf(listings.getNumber()));
        headers.add("per_pages", String.valueOf(listings.getNumberOfElements()));
        headers.add("total_pages", String.valueOf(listings.getTotalPages()));

        List<ListingSimpleOutDto> outDtos = listings.stream()
                .map(listingMapper::listingToSimpleOutDto)
                .toList();

        return new ResponseEntity<>(outDtos, headers, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@listingSecurity.isOwner(#id)")
    public ResponseEntity<SimpleMessageResponseDto> handlePut(
            @PathVariable long id,
            @RequestBody @Valid ListingUpdateDto dto,
            @AuthenticationPrincipal UserDetails principal) {
        log.info("Handling PUT /listings/{} request.", id);

        if (dto.id() != id) {
            throw new BadRequestException("Id mismatch between URL and body.");
        }

        CityWithParentCountyOutDto city = filterServiceClient.getCityById(dto.cityId());
        // Validate city exists
        if (city == null) {
            throw new BadRequestException("No city with id " + dto.cityId() + " exists.");
        }

        // Validate category exists
        if (filterServiceClient.getCategoryById(dto.categoryId()) == null) {
            throw new BadRequestException("No category with id " + dto.categoryId() + " exists.");
        }

        // Get user ID from auth service
        Long userId = authServiceClient.getUserIdByUsername(principal.getUsername());
        if (userId == null) {
            throw new BadRequestException("User not found: " + principal.getUsername());
        }

        Listing existingListing = listingService.findById(id);
        if (existingListing == null) {
            throw new ResourceNotFoundException("Listing with id " + id + " not found.");
        }

        Listing listing = listingMapper.updateDtoToListing(dto);
        listing.setCityId(city.id());
        listing.setCountyId(city.county().id());
        listing.setCountryId(city.county().country().id());
        listing.setCategoryId(dto.categoryId());
        listing.setOwnerId(userId);
        listing.setImages(existingListing.getImages());

        listingService.update(listing);
        var responseBody = new SimpleMessageResponseDto("Listing updated successfully.");
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@listingSecurity.isOwner(#id)")
    public ResponseEntity<SimpleMessageResponseDto> handleDelete(@PathVariable long id) {
        log.info("Handling DELETE /listings/{} request.", id);

        listingService.delete(id);

        var responseBody = new SimpleMessageResponseDto("Successfully deleted listing.");
        return new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);
    }

    private boolean isAnyFilterCriterionSet(FilterCriteria filterCriteria) {
        return (filterCriteria != null && (
                (filterCriteria.getTitle() != null && !filterCriteria.getTitle().isEmpty())
                        || filterCriteria.getMinPrice() != null
                        || filterCriteria.getMaxPrice() != null
                        || filterCriteria.getCountryId() != null
                        || filterCriteria.getCountyId() != null
                        || filterCriteria.getCityId() != null
                        || filterCriteria.getCategoryId() != null
        ));
    }
}
