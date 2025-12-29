package edu.bbte.replate.repository;

import edu.bbte.replate.dto.incoming.FilterCriteria;
import edu.bbte.replate.model.Category;
import edu.bbte.replate.model.Listing;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    default List<Listing> findListingsByFilters(FilterCriteria filterCriteria) {
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
                Predicate categoryPredicate = buildCategoryPredicateRecursive(root.get("category"), cb,
                        filterCriteria.getCategoryName(), 0, 5);
                predicates.add(categoryPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
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

    // Helper method to build recursive category predicate (basically filtering by category and its ancestors)
    // example: if filtering by "Electronics", also include listings in "Mobile Phones" if
    // "Mobile Phones" is a subcategory of "Electronics"
    private Predicate buildCategoryPredicateRecursive(Path<Category> categoryPath, CriteriaBuilder cb,
                                                      String targetCategoryName, int currentDepth, int maxDepth) {
        if (currentDepth > maxDepth) {
            return cb.disjunction(); // Return false predicate if max depth exceeded
        }

        // Check current category
        Predicate currentMatch = cb.equal(categoryPath.get("name"), targetCategoryName);

        // Check parent recursively
        Path<Category> parentPath = categoryPath.get("parentCategory");
        Predicate parentMatch = buildCategoryPredicateRecursive(parentPath, cb, targetCategoryName,
                currentDepth + 1, maxDepth);

        return cb.or(currentMatch, parentMatch);
    }
}
