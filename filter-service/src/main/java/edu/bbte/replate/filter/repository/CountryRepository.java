package edu.bbte.replate.filter.repository;

import edu.bbte.replate.filter.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findCountryByName(String name);
}
