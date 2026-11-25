package com.endava.insuranceapp.application.service.report;

import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;

public class GroupByCityStrategy implements PolicyGroupingStrategy {
    @Override
    public String groupKey(PolicyEntity p) {
        if (p.getBuilding() == null || p.getBuilding().getCity() == null) {
            return "UNKNOWN_CITY";
        }
        return p.getBuilding().getCity().getName();
    }
}
