package com.endava.insuranceapp.application.service.calculation;

import com.endava.insuranceapp.infrastructure.persistence.entity.*;
import com.endava.insuranceapp.infrastructure.persistence.repository.RiskFactorConfigurationRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
@Order(20)
public class RiskMultiplierStrategy implements PremiumMultiplierStrategy {

    private final RiskFactorConfigurationRepository riskRepo;

    public RiskMultiplierStrategy(RiskFactorConfigurationRepository riskRepo) {
        this.riskRepo = riskRepo;
    }

    @Override
    public BigDecimal multiplierAdjustment(PolicyCalculationContext ctx) {
        GeoContext geo = GeoContext.from(ctx.building());

        return riskRepo.findAll().stream()
                .filter(RiskFactorConfigurationEntity::isActive)
                .filter(r -> matches(r, geo))
                .map(RiskFactorConfigurationEntity::getAdjustmentPercentage)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean matches(RiskFactorConfigurationEntity r, GeoContext geo) {
        String lvl = r.getLevel() == null ? "" : r.getLevel().trim();

        if ("City".equalsIgnoreCase(lvl)) {
            return geo.cityId != null && geo.cityId.equals(r.getReferenceId());
        }
        if ("County".equalsIgnoreCase(lvl)) {
            return geo.countyId != null && geo.countyId.equals(r.getReferenceId());
        }
        if ("Country".equalsIgnoreCase(lvl)) {
            return geo.countryId != null && geo.countryId.equals(r.getReferenceId());
        }
        if ("BuildingType".equalsIgnoreCase(lvl)) {
            return geo.buildingType != null
                    && r.getBuildingType() != null
                    && geo.buildingType.equalsIgnoreCase(r.getBuildingType());
        }
        return false;
    }

    private static class GeoContext {
        final Long cityId;
        final Long countyId;
        final Long countryId;
        final String buildingType;

        private GeoContext(Long cityId, Long countyId, Long countryId, String buildingType) {
            this.cityId = cityId;
            this.countyId = countyId;
            this.countryId = countryId;
            this.buildingType = buildingType;
        }

        static GeoContext from(BuildingEntity building) {
            CityEntity city = building.getCity();
            CountyEntity county = city != null ? city.getCounty() : null;
            CountryEntity country = county != null ? county.getCountry() : null;

            Long cityId = city != null ? city.getId() : null;
            Long countyId = county != null ? county.getId() : null;
            Long countryId = country != null ? country.getId() : null;

            String buildingType = building.getBuildingType() != null
                    ? building.getBuildingType().name()
                    : null;

            return new GeoContext(cityId, countyId, countryId, buildingType);
        }
    }
}