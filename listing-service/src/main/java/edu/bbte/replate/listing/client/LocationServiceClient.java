package edu.bbte.replate.listing.client;

import edu.bbte.replate.shared.dto.outgoing.CategorySimpleOutDto;
import edu.bbte.replate.shared.dto.outgoing.CityWithParentCountyOutDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class LocationServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${location.service.url:http://location-service}")
    private String locationServiceUrl;

    public CityWithParentCountyOutDto getCityById(Long cityId) {
        log.info("Fetching city with id: {}", cityId);
        try {
            String url = locationServiceUrl + "/locations/cities/" + cityId;
            return restTemplate.getForObject(url, CityWithParentCountyOutDto.class);
        } catch (Exception e) {
            log.error("Failed to fetch city with id: {}", cityId, e);
            return null;
        }
    }

    public CategorySimpleOutDto getCategoryById(Long categoryId) {
        log.info("Fetching category with id: {}", categoryId);
        try {
            String url = locationServiceUrl + "/categories/" + categoryId;
            return restTemplate.getForObject(url, CategorySimpleOutDto.class);
        } catch (Exception e) {
            log.error("Failed to fetch category with id: {}", categoryId, e);
            return null;
        }
    }
}

