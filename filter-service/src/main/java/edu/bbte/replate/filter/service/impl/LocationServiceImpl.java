package edu.bbte.replate.filter.service.impl;

import edu.bbte.replate.filter.model.City;
import edu.bbte.replate.filter.model.Country;
import edu.bbte.replate.filter.model.County;
import edu.bbte.replate.filter.repository.CityRepository;
import edu.bbte.replate.filter.repository.CountryRepository;
import edu.bbte.replate.filter.repository.CountyRepository;
import edu.bbte.replate.filter.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CountyRepository countyRepository;
    @Autowired
    private CityRepository cityRepository;

    @Override
    public Country findCountryById(Long id) {
        log.info("Requested country with id: {}", id);
        return countryRepository.findById(id).orElse(null);
    }

    @Override
    public Country findCountryByName(String name) {
        log.info("Requested country with name: {}", name);
        return countryRepository.findCountryByName(name);
    }

    @Override
    public List<Country> findAllCountries() {
        log.info("Requested all countries");
        return countryRepository.findAll();
    }

    @Override
    public County findCountyById(Long id) {
        log.info("Requested county with id: {}", id);
        return countyRepository.findById(id).orElse(null);
    }

    @Override
    public County findCountyByNameAndCountry(String name, Country country) {
        log.info("Requested county with name: {}. From the country: {}", name, country.getName());
        return countyRepository.findCountyByNameAndCountry(name, country);
    }

    @Override
    public List<County> findCountiesByCountry(Country country) {
        log.info("Requested counties for country: {}", country.getName());
        Country fullyLoadedCountry = countryRepository.findById(country.getId()).orElse(null);
        return fullyLoadedCountry != null ? fullyLoadedCountry.getCounties() : null;
    }

    @Override
    public City findCityById(Long id) {
        log.info("Requested city with id: {}", id);
        return cityRepository.findById(id).orElse(null);
    }

    @Override
    public City findCityByNameAndCounty(String name, County county) {
        log.info("Requested city with name: {}. From the county: {}", name, county.getName());
        return cityRepository.findCityByNameAndCounty(name, county);
    }

    @Override
    public List<City> findCitiesByCounty(County county) {
        log.info("Requested cities for county: {}", county.getName());
        County fullyLoadedCounty = countyRepository.findById(county.getId()).orElse(null);
        return fullyLoadedCounty != null ? fullyLoadedCounty.getCities() : null;
    }
}
