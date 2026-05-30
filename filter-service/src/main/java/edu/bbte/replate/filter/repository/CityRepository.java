package edu.bbte.replate.filter.repository;

import edu.bbte.replate.filter.model.City;
import edu.bbte.replate.filter.model.County;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
    City findCityByNameAndCounty(String name, County county);
}
