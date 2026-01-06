package edu.bbte.replate.service.impl;

import edu.bbte.replate.dto.incoming.FilterCriteria;
import edu.bbte.replate.dto.incoming.ListingCreateDto;
import edu.bbte.replate.mapper.ListingMapper;
import edu.bbte.replate.model.Category;
import edu.bbte.replate.model.City;
import edu.bbte.replate.model.Listing;
import edu.bbte.replate.model.User;
import edu.bbte.replate.repository.ListingRepository;
import edu.bbte.replate.service.CategoryService;
import edu.bbte.replate.service.ListingService;
import edu.bbte.replate.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ListingServiceImpl implements ListingService {
    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ListingMapper listingMapper;

    @Override
    public Listing findById(Long id) {
        log.info("Requested listing with id: {}", id);
        return listingRepository.findById(id).orElse(null);
    }

    @Override
    public List<Listing> findAll() {
        log.info("Requested all listings");
        return listingRepository.findAll();
    }

    @Override
    public List<Listing> findByFilters(FilterCriteria filters) {
        log.info("Requested listing by filters: {}", filters);
        return listingRepository.findListingsByFilters(filters);
    }

    @Override
    public Listing create(ListingCreateDto dto, User user) {
        log.info("Attempting to create listing: {}", dto);

        City city = locationService.findCityById(dto.cityId());
        Category category = categoryService.findById(dto.categoryId());

        Listing listing = listingMapper.createDtoToListing(dto);

        listing.setCity(city);
        listing.setCategory(category);
        listing.setOwner(user);

        return listingRepository.saveAndFlush(listing);
    }

    @Override
    public void update(Listing listing, User user) {
        log.info("Attempting to update listing: {}", listing);
        listingRepository.saveAndFlush(listing);
    }

    @Override
    public void delete(Long id, User user) {
        log.info("Attempting to delete listing with id: {}", id);
        listingRepository.deleteById(id);
    }
}
