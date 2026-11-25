package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.*;
import com.endava.insuranceapp.infrastructure.persistence.repository.BuildingRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.CityRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.ClientRepository;
import com.endava.insuranceapp.web.dto.BuildingCreateRequest;
import com.endava.insuranceapp.web.dto.BuildingUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildingServiceTest {

    private BuildingRepository buildingRepository;
    private ClientRepository clientRepository;
    private CityRepository cityRepository;

    private BuildingService buildingService;

    @BeforeEach
    void setUp() {
        buildingRepository = mock(BuildingRepository.class);
        clientRepository = mock(ClientRepository.class);
        cityRepository = mock(CityRepository.class);
        buildingService = new BuildingService(buildingRepository, clientRepository, cityRepository);
    }

    @Test
    void create_whenClientMissing_throws404() {
        when(clientRepository.findById(10L)).thenReturn(Optional.empty());

        BuildingCreateRequest req = new BuildingCreateRequest(
                1L, "Street", "10", 2000, "RESIDENTIAL", 2,
                new BigDecimal("100.00"), new BigDecimal("200000.00"), null
        );

        assertThrows(ResourceNotFoundException.class, () -> buildingService.create(10L, req));
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void create_whenCityMissing_throws404() {
        ClientEntity client = new ClientEntity();
        client.setType(ClientType.INDIVIDUAL);
        client.setName("John");
        client.setIdentifier("ID1");

        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
        when(cityRepository.findById(999L)).thenReturn(Optional.empty());

        BuildingCreateRequest req = new BuildingCreateRequest(
                999L, "Street", "10", 2000, "RESIDENTIAL", 2,
                new BigDecimal("100.00"), new BigDecimal("200000.00"), null
        );

        assertThrows(ResourceNotFoundException.class, () -> buildingService.create(10L, req));
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void create_whenBuildingTypeInvalid_throws400IllegalArgument() {
        ClientEntity client = new ClientEntity();
        client.setType(ClientType.INDIVIDUAL);
        client.setName("John");
        client.setIdentifier("ID1");

        CityEntity city = new CityEntity();
        city.setName("Cluj-Napoca");

        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));

        BuildingCreateRequest req = new BuildingCreateRequest(
                1L, "Street", "10", 2000, "NOT_A_TYPE", 2,
                new BigDecimal("100.00"), new BigDecimal("200000.00"), null
        );

        assertThrows(IllegalArgumentException.class, () -> buildingService.create(10L, req));
        verify(buildingRepository, never()).save(any());
    }

    @Test
    void update_doesNotChangeClient_evenIfDifferentClientExistsElsewhere() {
        ClientEntity clientA = new ClientEntity();
        clientA.setType(ClientType.INDIVIDUAL);
        clientA.setName("A");
        clientA.setIdentifier("A1");

        CountryEntity country = new CountryEntity();
        country.setName("Romania");

        CountyEntity county = new CountyEntity();
        county.setName("Cluj");
        county.setCountry(country);

        CityEntity city = new CityEntity();
        city.setName("Cluj-Napoca");
        city.setCounty(county);

        BuildingEntity existing = new BuildingEntity();
        existing.setClient(clientA);
        existing.setCity(city);
        existing.setStreet("Old");
        existing.setNumber("1");
        existing.setBuildingType(BuildingType.RESIDENTIAL);
        existing.setSurfaceArea(new BigDecimal("10"));
        existing.setInsuredValue(new BigDecimal("100"));

        when(buildingRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(buildingRepository.save(any(BuildingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: update building (notice: no clientId in request, so client cannot be changed)
        BuildingUpdateRequest req = new BuildingUpdateRequest(
                1L, "New Street", "99", 2010, "OFFICE", 3,
                new BigDecimal("123.45"), new BigDecimal("99999.00"), "none"
        );

        var resp = buildingService.update(5L, req);

        // Assert: client remains A
        assertEquals(existing.getClient().getIdentifier(), clientA.getIdentifier());
        assertEquals("New Street", resp.street());
        assertEquals("OFFICE", resp.buildingType());
    }

    @Test
    void update_whenBuildingMissing_throws404() {
        when(buildingRepository.findById(123L)).thenReturn(Optional.empty());

        BuildingUpdateRequest req = new BuildingUpdateRequest(
                1L, "Street", "10", 2000, "RESIDENTIAL", 2,
                new BigDecimal("100.00"), new BigDecimal("200000.00"), null
        );

        assertThrows(ResourceNotFoundException.class, () -> buildingService.update(123L, req));
    }
}
