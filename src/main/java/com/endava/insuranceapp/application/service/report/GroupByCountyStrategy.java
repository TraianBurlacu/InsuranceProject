package com.endava.insuranceapp.application.service.report;

import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;

public class GroupByCountyStrategy implements PolicyGroupingStrategy {
    @Override
    public String groupKey(PolicyEntity p) {
        if (p.getBuilding() == null
                || p.getBuilding().getCity() == null
                || p.getBuilding().getCity().getCounty() == null) {
            return "UNKNOWN_COUNTY";
        }
        return p.getBuilding().getCity().getCounty().getName();
    }
}
