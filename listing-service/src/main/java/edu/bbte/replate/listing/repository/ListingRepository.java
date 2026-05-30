package edu.bbte.replate.listing.repository;

import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.shared.dto.incoming.FilterCriteria;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    default Page<Listing> findListingsByFilters(FilterCriteria filterCriteria, Pageable pageable) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by title
            if (filterCriteria.getTitle() != null) {
                predicates.add(cb.like(root.get("title"), "%" + filterCriteria.getTitle() + "%"));
            }

            // Filter by price
            if (filterCriteria.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filterCriteria.getMinPrice()));
            }
            if (filterCriteria.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filterCriteria.getMaxPrice()));
            }

            // Filter by location
            predicates.add(buildLocationPredicate(root, cb, filterCriteria.getCountryId(),
                    filterCriteria.getCountyId(), filterCriteria.getCityId()));

            // Filter by category
            if (filterCriteria.getCategoryId() != null) {
                Predicate categoryPredicate = buildCategoryPredicate(root, cb, filterCriteria.getCategoryId());
                predicates.add(categoryPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    // Helper method to build location predicate based on country, county, and city
    // Always filters hierarchically: country -> county -> city
    private Predicate buildLocationPredicate(Path<Listing> root, CriteriaBuilder cb, Long countryId,
                                             Long countyId, Long cityId) {
        List<Predicate> locationPredicates = new ArrayList<>();

        boolean filterByCountry = countryId != null;
        boolean filterByCounty = filterByCountry && countyId != null;
        boolean filterByCity = filterByCounty && cityId != null;

        if (filterByCountry) {
            locationPredicates.add(cb.equal(root.get("country_id"), countryId));
        }

        if (filterByCounty) {
            locationPredicates.add(cb.equal(root.get("county_id"), countyId));
        }

        if (filterByCity) {
            locationPredicates.add(cb.equal(root.get("city_id"), cityId));
        }

        return cb.and(locationPredicates.toArray(new Predicate[0]));
    }

    // Helper method to build category predicate (basically filtering by category and its ancestors)
    // example: if filtering by "Electronics", also include listings in "Mobile Phones" if
    // "Mobile Phones" is a subcategory of "Electronics"
    private Predicate buildCategoryPredicate(Root<Listing> root,
                                             CriteriaBuilder cb,
                                             Long categoryId) {
        return cb.and(cb.equal(root.get("category").get("id"), categoryId));
    }
}
