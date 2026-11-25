package com.endava.insuranceapp.web.dto;

import java.math.BigDecimal;

public record BuildingResponse(
        Long id,
        Long clientId,

        Long cityId,
        String cityName,
        String countyName,
        String countryName,

        String street,
        String number,
        Integer constructionYear,
        String buildingType,
        Integer floors,
        BigDecimal surfaceArea,
        BigDecimal insuredValue,
        String riskIndicators
) {}
