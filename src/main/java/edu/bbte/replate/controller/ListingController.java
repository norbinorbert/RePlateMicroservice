package edu.bbte.replate.controller;

import edu.bbte.replate.dto.incoming.ListingCreateDto;
import edu.bbte.replate.mapper.ListingMapper;
import edu.bbte.replate.model.Category;
import edu.bbte.replate.model.City;
import edu.bbte.replate.model.Listing;
import edu.bbte.replate.model.User;
import edu.bbte.replate.service.CategoryService;
import edu.bbte.replate.service.ListingService;
import edu.bbte.replate.service.LocationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
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
    private ListingMapper listingMapper;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> handlePost(
            @RequestBody @Valid ListingCreateDto dto,
            @AuthenticationPrincipal User user) {
        log.info("Handling POST /listings request");

        Map<String, String> responseBody = new HashMap<>();

        City city = locationService.findCityById(dto.cityId());
        Category category = categoryService.findById(dto.categoryId());

        Listing listing = listingMapper.createDtoToListing(dto);
        listing.setCity(city);
        listing.setCategory(category);
        listing.setOwner(user);

        Listing createdListing = listingService.create(listing);

        // Send the created entity's access path in the Location header
        URI createdUri = URI.create("/listings/" + createdListing);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(createdUri);

        responseBody.put("Message", "New listing created successfully.");

        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.CREATED);
    }
}
