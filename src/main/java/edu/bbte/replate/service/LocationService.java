package edu.bbte.replate.service;

import edu.bbte.replate.model.City;
import edu.bbte.replate.model.Country;
import edu.bbte.replate.model.County;

import java.util.List;

public interface LocationService {
    Country findCountryById(Long id);

    Country findCountryByName(String name);

    List<Country> findAllCountries();

    County findCountyById(Long id);

    County findCountyByNameAndCountry(String name, Country country);

    List<County> findCountiesByCountry(Country country);

    City findCityById(Long id);

    City findCityByNameAndCounty(String name, County county);

    List<City> findCitiesByCounty(County county);
}
