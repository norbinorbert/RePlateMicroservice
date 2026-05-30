package edu.bbte.replate.listing.service;

import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.shared.dto.incoming.FilterCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListingService {
    Listing findById(Long id);

    Page<Listing> findAll(Pageable pageable);

    Page<Listing> findByFilters(FilterCriteria filters, Pageable pageable);

    Listing create(Listing listing);

    void update(Listing listing);

    void delete(Long id);
}
