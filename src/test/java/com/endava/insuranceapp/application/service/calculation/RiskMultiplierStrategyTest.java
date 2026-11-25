package com.endava.insuranceapp.application.service.calculation;

import com.endava.insuranceapp.infrastructure.persistence.entity.*;
import com.endava.insuranceapp.infrastructure.persistence.repository.RiskFactorConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RiskMultiplierStrategyTest {

    private RiskFactorConfigurationRepository riskRepo;
    private RiskMultiplierStrategy strategy;

    @BeforeEach
    void setUp() {
        riskRepo = mock(RiskFactorConfigurationRepository.class);
        strategy = new RiskMultiplierStrategy(riskRepo);
    }

    @Test
    void multiplierAdjustment_matchesCityCountyCountryAndBuildingType() {
        CountryEntity country = new CountryEntity();
        country.setId(10L);
        country.setName("Romania");

        CountyEntity county = new CountyEntity();
        county.setId(20L);
        county.setName("Cluj");
        county.setCountry(country);

        CityEntity city = new CityEntity();
        city.setId(30L);
        city.setName("Cluj-Napoca");
        city.setCounty(county);

        BuildingEntity building = new BuildingEntity();
        building.setCity(city);
        building.setBuildingType(BuildingType.RESIDENTIAL);

        // Active matching factors
        RiskFactorConfigurationEntity cityRf = rf(true, "City", 30L, null, new BigDecimal("0.01"));
        RiskFactorConfigurationEntity countyRf = rf(true, "County", 20L, null, new BigDecimal("0.02"));
        RiskFactorConfigurationEntity countryRf = rf(true, "Country", 10L, null, new BigDecimal("0.03"));
        RiskFactorConfigurationEntity typeRf = rf(true, "BuildingType", null, "RESIDENTIAL", new BigDecimal("0.04"));

        // Non-matching / inactive
        RiskFactorConfigurationEntity wrongCity = rf(true, "City", 999L, null, new BigDecimal("0.50"));
        RiskFactorConfigurationEntity inactive = rf(false, "County", 20L, null, new BigDecimal("0.60"));

        when(riskRepo.findAll()).thenReturn(List.of(cityRf, countyRf, countryRf, typeRf, wrongCity, inactive));

        PolicyCalculationContext ctx = new PolicyCalculationContext(LocalDate.now(), building, new BrokerEntity());
        BigDecimal res = strategy.multiplierAdjustment(ctx);

        // sum = 0.01 + 0.02 + 0.03 + 0.04 = 0.10
        assertEquals(new BigDecimal("0.10"), res);
    }

    @Test
    void multiplierAdjustment_nullGeo_doesNotThrow_andReturnsZeroIfNoBuildingTypeMatch() {
        BuildingEntity building = new BuildingEntity(); // city null, buildingType null

        RiskFactorConfigurationEntity cityRf = rf(true, "City", 1L, null, new BigDecimal("0.10"));
        RiskFactorConfigurationEntity typeRf = rf(true, "BuildingType", null, "RESIDENTIAL", new BigDecimal("0.20"));

        when(riskRepo.findAll()).thenReturn(List.of(cityRf, typeRf));

        PolicyCalculationContext ctx = new PolicyCalculationContext(LocalDate.now(), building, new BrokerEntity());
        BigDecimal res = strategy.multiplierAdjustment(ctx);

        assertEquals(BigDecimal.ZERO, res);
    }

    private RiskFactorConfigurationEntity rf(boolean active, String level, Long refId, String buildingType, BigDecimal adj) {
        RiskFactorConfigurationEntity r = new RiskFactorConfigurationEntity();
        r.setActive(active);
        r.setLevel(level);
        r.setReferenceId(refId);
        r.setBuildingType(buildingType);
        r.setAdjustmentPercentage(adj);
        return r;
    }
}