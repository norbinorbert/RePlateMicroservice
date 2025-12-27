package edu.bbte.replate.repository;

import edu.bbte.replate.model.City;
import edu.bbte.replate.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
    City findCityByNameAndCounty(String name, County county);
}
