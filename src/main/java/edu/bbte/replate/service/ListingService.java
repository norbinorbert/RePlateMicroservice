package edu.bbte.replate.service;

import edu.bbte.replate.dto.incoming.FilterCriteria;
import edu.bbte.replate.dto.incoming.ListingCreateDto;
import edu.bbte.replate.model.Listing;
import edu.bbte.replate.model.User;

import java.util.List;

public interface ListingService {
    Listing findById(Long id);

    List<Listing> findAll();

    List<Listing> findByFilters(FilterCriteria filters);

    Listing create(Listing listing);

    void update(Listing listing);

    void delete(Long id);
}
