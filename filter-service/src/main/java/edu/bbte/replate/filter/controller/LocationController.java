package edu.bbte.replate.filter.controller;

import edu.bbte.replate.filter.mapper.*;
import edu.bbte.replate.filter.model.City;
import edu.bbte.replate.filter.model.Country;
import edu.bbte.replate.filter.model.County;
import edu.bbte.replate.filter.service.LocationService;
import edu.bbte.replate.shared.dto.outgoing.*;
import edu.bbte.replate.shared.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@Slf4j
public class LocationController {
    @Autowired
    private LocationService locationService;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private CountryAggregateMapper countryAggregateMapper;

    @Autowired
    private CountyMapper countyMapper;

    @Autowired
    private CountyAggregateMapper countyAggregateMapper;

    @Autowired
    private CityMapper cityMapper;

    @GetMapping("/countries")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CountrySimpleOutDto>> handleGetAllCountries() {
        log.info("Handling GET /locations/countries request.");

        List<Country> countries = locationService.findAllCountries();

        List<CountrySimpleOutDto> outDtos = countries
                .stream()
                .map(countryMapper::toSimpleOutDto)
                .toList();

        return ResponseEntity.ok(outDtos);
    }

    @GetMapping("/countries/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CountryWithChildCountiesOutDto> handleGetCountryById(@PathVariable long id) {
        log.info("Handling GET /locations/countries/{} request.", id);

        Country country = locationService.findCountryById(id);

        if (country == null) {
            throw new ResourceNotFoundException("No country with id " + id + " was found.");
        }

        CountryWithChildCountiesOutDto dto = countryAggregateMapper.toWithChildrenDto(country);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/countries/{countryId}/counties")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CountySimpleOutDto>> handleGetAllCountiesByCountryId(@PathVariable long countryId) {
        log.info("Handling GET locations/countries/{}/counties request.", countryId);

        Country country = locationService.findCountryById(countryId);

        if (country == null) {
            throw new ResourceNotFoundException("No country with id " + countryId + " was found.");
        }

        List<County> counties = locationService.findCountiesByCountry(country);

        List<CountySimpleOutDto> outDtos = counties
                .stream()
                .map(countyMapper::toSimpleOutDto)
                .toList();

        return ResponseEntity.ok(outDtos);
    }

    @GetMapping("/countries/{countryId}/counties/{countyId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CountyWithChildCitiesOutDto> handleGetCountyById(
            @PathVariable long countryId,
            @PathVariable long countyId
    ) {
        log.info("Handling GET /countries/{}/counties/{}", countryId, countyId);

        Country country = locationService.findCountryById(countryId);

        if (country == null) {
            throw new ResourceNotFoundException("No country with id " + countryId + " was found.");
        }

        County county = locationService.findCountyById(countyId);

        if (county == null) {
            throw new ResourceNotFoundException("No county with id " + countyId + " was found.");
        }

        CountyWithChildCitiesOutDto dto = countyAggregateMapper.toWithChildrenDto(county);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/countries/{countryId}/counties/{countyId}/cities")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CitySimpleOutDto>> handleGetAllCitiesByCountyId(
            @PathVariable long countryId,
            @PathVariable long countyId
    ) {
        log.info("Handling GET /countries/{}/counties/{}/cities request.", countryId, countyId);

        Country country = locationService.findCountryById(countryId);

        if (country == null) {
            throw new ResourceNotFoundException("No country with id " + countryId + " was found.");
        }

        County county = locationService.findCountyById(countyId);

        if (county == null) {
            throw new ResourceNotFoundException("No county with id " + countyId + " was found.");
        }

        List<City> cities = locationService.findCitiesByCounty(county);

        List<CitySimpleOutDto> outDtos = cities
                .stream()
                .map(cityMapper::toSimpleOutDto)
                .toList();

        return ResponseEntity.ok(outDtos);
    }

    @GetMapping("/cities/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CityWithParentCountyOutDto> handleGetCityById(
            @PathVariable long cityId
    ) {
        log.info("Handling GET /cities/{} request.", cityId);

        City city = locationService.findCityById(cityId);
        if (city == null) {
            throw new ResourceNotFoundException("No city with id " + cityId + " was found.");
        }

        CityWithParentCountyOutDto dto = cityMapper.toWithParentOutDto(city);

        return ResponseEntity.ok(dto);
    }
}
