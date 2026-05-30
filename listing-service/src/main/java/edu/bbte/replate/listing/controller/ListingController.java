package edu.bbte.replate.listing.controller;

import edu.bbte.replate.listing.mapper.ListingMapper;
import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.listing.service.ListingService;
import edu.bbte.replate.shared.dto.incoming.FilterCriteria;
import edu.bbte.replate.shared.dto.incoming.ListingCreateDto;
import edu.bbte.replate.shared.dto.incoming.ListingUpdateDto;
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
    private LocationService locationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ListingMapper listingMapper;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SimpleMessageResponseDto> handlePost(
            @RequestBody @Valid ListingCreateDto dto,
            @AuthenticationPrincipal UserDetails principal
    ) {
        log.info("Handling POST /listings request.");

        City city = locationService.findCityById(dto.cityId());
        if (city == null) {
            throw new BadRequestException("No city with id " + dto.cityId() + " exists.");
        }

        Category category = categoryService.findById(dto.categoryId());
        if (category == null) {
            throw new BadRequestException("No category with id " + dto.categoryId() + " exists.");
        }

        Listing listing = listingMapper.createDtoToListing(dto);
        listing.setCity(city);
        listing.setCategory(category);
        User user = userService.findByUsername(principal.getUsername());
        listing.setOwner(user);

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

        if (filterCriteria == null || !isAnyFilterCriterionSet(filterCriteria)) {
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

        City city = locationService.findCityById(dto.cityId());
        if (city == null) {
            throw new BadRequestException("No city with id " + dto.cityId() + " exists.");
        }

        Category category = categoryService.findById(dto.categoryId());
        if (category == null) {
            throw new BadRequestException("No category with id " + dto.categoryId() + " exists.");
        }

        Listing listing = listingMapper.updateDtoToListing(dto);
        listing.setCity(city);
        listing.setCategory(category);
        listing.setImages(listingService.findById(listing.getId()).getImages());
        User user = userService.findByUsername(principal.getUsername());
        listing.setOwner(user);

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
        return (filterCriteria.getTitle() != null && !filterCriteria.getTitle().isEmpty())
                || filterCriteria.getMinPrice() != null
                || filterCriteria.getMaxPrice() != null
                || filterCriteria.getCountryId() != null
                || filterCriteria.getCountyId() != null
                || filterCriteria.getCityId() != null
                || filterCriteria.getCategoryId() != null;
    }
}
