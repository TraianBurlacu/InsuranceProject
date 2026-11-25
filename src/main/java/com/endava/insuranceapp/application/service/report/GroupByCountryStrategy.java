package com.endava.insuranceapp.application.service.report;

import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;

public class GroupByCountryStrategy implements PolicyGroupingStrategy {
    @Override
    public String groupKey(PolicyEntity p) {
        if (p.getBuilding() == null
                || p.getBuilding().getCity() == null
                || p.getBuilding().getCity().getCounty() == null
                || p.getBuilding().getCity().getCounty().getCountry() == null) {
            return "UNKNOWN_COUNTRY";
        }
        return p.getBuilding().getCity().getCounty().getCountry().getName();
    }
}
