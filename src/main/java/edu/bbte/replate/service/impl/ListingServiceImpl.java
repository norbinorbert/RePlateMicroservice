package edu.bbte.replate.service.impl;

import edu.bbte.replate.dto.incoming.FilterCriteria;
import edu.bbte.replate.exception.ResourceNotFoundException;
import edu.bbte.replate.model.Listing;
import edu.bbte.replate.repository.ListingRepository;
import edu.bbte.replate.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ListingServiceImpl implements ListingService {
    @Autowired
    private ListingRepository listingRepository;

    @Override
    public Listing findById(Long id) {
        log.info("Requested listing with id: {}", id);
        return listingRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Listing> findAll(Pageable pageable) {
        log.info("Requested all listings");
        return listingRepository.findAll(pageable);
    }

    @Override
    public Page<Listing> findByFilters(FilterCriteria filters, Pageable pageable) {
        log.info("Requested listing by filters: {}", filters);
        return listingRepository.findListingsByFilters(filters, pageable);
    }

    @Override
    public Listing create(Listing listing) {
        log.info("Attempting to create listing: {}", listing);
        return listingRepository.saveAndFlush(listing);
    }

    @Override
    public void update(Listing listing) {
        log.info("Attempting to update listing: {}", listing);
        listingRepository.saveAndFlush(listing);
    }

    @Override
    public void delete(Long id) {
        log.info("Attempting to delete listing with id: {}", id);
        listingRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Listing with id " + id + " not found.")
        );
        listingRepository.deleteById(id);
    }
}
