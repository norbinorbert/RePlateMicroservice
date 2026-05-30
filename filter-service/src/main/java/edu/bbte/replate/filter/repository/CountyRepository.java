package edu.bbte.replate.filter.repository;

import edu.bbte.replate.filter.model.Country;
import edu.bbte.replate.filter.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountyRepository extends JpaRepository<County, Long> {
    County findCountyByNameAndCountry(String name, Country country);
}
