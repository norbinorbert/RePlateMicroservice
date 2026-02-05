package edu.bbte.replate.repository;

import edu.bbte.replate.dto.incoming.FilterCriteria;
import edu.bbte.replate.model.Category;
import edu.bbte.replate.model.Listing;
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
            predicates.add(buildLocationPredicate(root, cb, filterCriteria.getCountryName(),
                    filterCriteria.getCountyName(), filterCriteria.getCityName()));

            // Filter by category
            if (filterCriteria.getCategoryName() != null) {
                Predicate categoryPredicate = buildCategoryPredicate(root, cb, filterCriteria.getCategoryName());
                predicates.add(categoryPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    // Helper method to build location predicate based on country, county, and city
    // Always filters hierarchically: country -> county -> city
    private Predicate buildLocationPredicate(Path<Listing> root, CriteriaBuilder cb, String countryName,
                                             String countyName, String cityName) {
        List<Predicate> locationPredicates = new ArrayList<>();

        boolean filterByCountry = countryName != null;
        boolean filterByCounty = filterByCountry && countyName != null;
        boolean filterByCity = filterByCounty && cityName != null;

        if (filterByCountry) {
            locationPredicates.add(cb.equal(root.get("city").get("county").get("country").get("name"), countryName));
        }

        if (filterByCounty) {
            locationPredicates.add(cb.equal(root.get("city").get("county").get("name"), countyName));
        }

        if (filterByCity) {
            locationPredicates.add(cb.equal(root.get("city").get("name"), cityName));
        }

        return cb.and(locationPredicates.toArray(new Predicate[0]));
    }

    // Helper method to build category predicate (basically filtering by category and its ancestors)
    // example: if filtering by "Electronics", also include listings in "Mobile Phones" if
    // "Mobile Phones" is a subcategory of "Electronics"
    private Predicate buildCategoryPredicate(Root<Listing> root,
                                             CriteriaBuilder cb,
                                             String categoryName) {

        Join<Listing, Category> c0 = root.join("category", JoinType.LEFT);
        Join<Category, Category> c1 = c0.join("parentCategory", JoinType.LEFT);
        Join<Category, Category> c2 = c1.join("parentCategory", JoinType.LEFT);
        Join<Category, Category> c3 = c2.join("parentCategory", JoinType.LEFT);
        Join<Category, Category> c4 = c3.join("parentCategory", JoinType.LEFT);
        Join<Category, Category> c5 = c4.join("parentCategory", JoinType.LEFT);

        return cb.or(
                cb.equal(c0.get("name"), categoryName),
                cb.equal(c1.get("name"), categoryName),
                cb.equal(c2.get("name"), categoryName),
                cb.equal(c3.get("name"), categoryName),
                cb.equal(c4.get("name"), categoryName),
                cb.equal(c5.get("name"), categoryName)
        );
    }
}
