package com.endava.insuranceapp.application.service;


import com.endava.insuranceapp.infrastructure.persistence.entity.CityEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.CountyEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.CountryEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.CityRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.CountryRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.CountyRepository;
import com.endava.insuranceapp.web.dto.CityResponse;
import com.endava.insuranceapp.web.dto.CountyResponse;
import com.endava.insuranceapp.web.dto.CountryResponse;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GeographyServiceTest {

    CountryRepository countryRepository;
    CountyRepository countyRepository;
    CityRepository cityRepository;
    GeographyService geographyService;

    @BeforeEach
    void setUp() {
        countryRepository = mock(CountryRepository.class);
        countyRepository = mock(CountyRepository.class);
        cityRepository = mock(CityRepository.class);
        geographyService = new GeographyService(countryRepository, countyRepository, cityRepository);
    }

    @Test
    void getCountries_returnsMappedResponses() {
        CountryEntity rom = new CountryEntity();
        rom.setName("Romania");
        when(countryRepository.findAll()).thenReturn(List.of(rom));

        List<CountryResponse> result = geographyService.getCountries();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Romania");
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void getCountiesByCountry_whenCountryExists_returnsCounties() {
        when(countryRepository.existsById(1L)).thenReturn(true);

        CountyEntity c = new CountyEntity();
        c.setName("Cluj");
        when(countyRepository.findByCountryIdOrderByNameAsc(1L)).thenReturn(List.of(c));

        List<CountyResponse> counties = geographyService.getCountiesByCountry(1L);

        assertThat(counties).hasSize(1);
        assertThat(counties.get(0).name()).isEqualTo("Cluj");
        verify(countyRepository).findByCountryIdOrderByNameAsc(1L);
    }

    @Test
    void getCountiesByCountry_whenCountryMissing_throws404() {
        when(countryRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> geographyService.getCountiesByCountry(99L));
        verify(countyRepository, never()).findByCountryIdOrderByNameAsc(anyLong());
    }

    @Test
    void getCitiesByCounty_whenCountyExists_returnsCities() {
        when(countyRepository.existsById(2L)).thenReturn(true);

        CityEntity city = new CityEntity();
        city.setName("Cluj-Napoca");
        when(cityRepository.findByCountyIdOrderByNameAsc(2L)).thenReturn(List.of(city));

        List<CityResponse> cities = geographyService.getCitiesByCounty(2L);
        assertThat(cities).hasSize(1);
        assertThat(cities.get(0).name()).isEqualTo("Cluj-Napoca");
    }

    @Test
    void getCitiesByCounty_whenCountyMissing_throws404() {
        when(countyRepository.existsById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> geographyService.getCitiesByCounty(2L));
        verify(cityRepository, never()).findByCountyIdOrderByNameAsc(anyLong());
    }
}
