package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.repository.CityRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.CountryRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.CountyRepository;
import com.endava.insuranceapp.web.dto.CityResponse;
import com.endava.insuranceapp.web.dto.CountryResponse;
import com.endava.insuranceapp.web.dto.CountyResponse;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class GeographyService {

    private static final Logger log = LoggerFactory.getLogger(GeographyService.class);


    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final CityRepository cityRepository;

    public GeographyService(CountryRepository countryRepository,
                            CountyRepository countyRepository,
                            CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.countyRepository = countyRepository;
        this.cityRepository = cityRepository;
    }

    public List<CountryResponse> getCountries() {
        return countryRepository.findAll().stream()
                .map(c -> new CountryResponse(c.getId(), c.getName()))
                .toList();
    }

    public List<CountyResponse> getCountiesByCountry(Long countryId) {
        if (!countryRepository.existsById(countryId)) {
            log.warn("Country with id {} not found", countryId);
            throw new ResourceNotFoundException("Country with id " + countryId + " not found");
        }

        log.info("Listed all counties from country with id = {}", countryId);
        return countyRepository.findByCountryIdOrderByNameAsc(countryId).stream()
                .map(c -> new CountyResponse(c.getId(), c.getName()))
                .toList();
    }

    public List<CityResponse> getCitiesByCounty(Long countyId) {
        if (!countyRepository.existsById(countyId)) {
            log.warn("County with id {} not found", countyId);
            throw new ResourceNotFoundException("County with id " + countyId + " not found");
        }
        log.info("Listed all cities from county with id = {}", countyId);
        return cityRepository.findByCountyIdOrderByNameAsc(countyId).stream()
                .map(c -> new CityResponse(c.getId(), c.getName()))
                .toList();
    }
}
