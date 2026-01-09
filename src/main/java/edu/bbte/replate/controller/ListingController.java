package edu.bbte.replate.controller;

import edu.bbte.replate.dto.incoming.FilterCriteria;
import edu.bbte.replate.dto.incoming.ListingCreateDto;
import edu.bbte.replate.dto.incoming.ListingUpdateDto;
import edu.bbte.replate.dto.outgoing.ListingDetailedOutDto;
import edu.bbte.replate.dto.outgoing.ListingSimpleOutDto;
import edu.bbte.replate.exception.BadRequestException;
import edu.bbte.replate.exception.ResourceNotFoundException;
import edu.bbte.replate.mapper.ListingMapper;
import edu.bbte.replate.model.Category;
import edu.bbte.replate.model.City;
import edu.bbte.replate.model.Listing;
import edu.bbte.replate.model.User;
import edu.bbte.replate.service.CategoryService;
import edu.bbte.replate.service.ListingService;
import edu.bbte.replate.service.LocationService;
import edu.bbte.replate.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> handlePost(
            @RequestBody @Valid ListingCreateDto dto,
            @AuthenticationPrincipal UserDetails principal
    ) {
        log.info("Handling POST /listings request.");

        Map<String, String> responseBody = new HashMap<>();

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

        responseBody.put("Message", "New listing created successfully.");

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
            @RequestBody(required = false) @Valid FilterCriteria filterCriteria
    ) {
        log.info("Handling GET /listings request.");

        List<Listing> listings;

        if (filterCriteria == null || !isAnyFilterCriterionSet(filterCriteria)) {
            listings = listingService.findAll();
        } else {
            listings = listingService.findByFilters(filterCriteria);
        }

        List<ListingSimpleOutDto> outDtos = listings.stream()
                .map(l -> listingMapper.listingToSimpleOutDto(l))
                .toList();

        return new ResponseEntity<>(outDtos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@listingSecurity.isOwner(#id)")
    public ResponseEntity<ListingDetailedOutDto> handlePut(
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
        User user = userService.findByUsername(principal.getUsername());
        listing.setOwner(user);

        // Return different responses whether the listing existed already
        Listing preExistingListing = listingService.findById(id);
        if (preExistingListing != null) {
            listingService.update(listing);
            return ResponseEntity.ok(listingMapper.listingToDetailedOutDto(listing));
        }

        Listing createdListing = listingService.create(listing);

        // Send the created entity's access path in the Location header
        URI createdUri = URI.create("/listings/" + createdListing.getId());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(createdUri);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(listingMapper.listingToDetailedOutDto(createdListing));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@listingSecurity.isOwner(#id)")
    public ResponseEntity<Map<String, String>> handleDelete(@PathVariable long id) {
        log.info("Handling DELETE /listings/{} request.", id);
        Map<String, String> responseBody = new HashMap<>();

        listingService.delete(id);

        responseBody.put("Message", "Successfully deleted listing.");
        return new ResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);
    }

    private boolean isAnyFilterCriterionSet(FilterCriteria filterCriteria) {
        return (filterCriteria.getTitle() != null && !filterCriteria.getTitle().isEmpty())
                || filterCriteria.getMinPrice() != null
                || filterCriteria.getMaxPrice() != null
                || (filterCriteria.getCountryName() != null && !filterCriteria.getCountryName().isEmpty())
                || (filterCriteria.getCountyName() != null && !filterCriteria.getCountyName().isEmpty())
                || (filterCriteria.getCityName() != null && !filterCriteria.getCityName().isEmpty())
                || (filterCriteria.getCategoryName() != null && !filterCriteria.getCategoryName().isEmpty());
    }
}
