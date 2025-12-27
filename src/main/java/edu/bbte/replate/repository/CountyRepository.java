package edu.bbte.replate.repository;

import edu.bbte.replate.model.Country;
import edu.bbte.replate.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountyRepository extends JpaRepository<County, Long> {
    County findCountyByNameAndCountry(String name, Country country);
}
