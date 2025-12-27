package edu.bbte.replate.repository;

import edu.bbte.replate.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findCountryByName(String name);
}
