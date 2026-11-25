package com.endava.insuranceapp.application.service.report;

import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;

public class GroupByBrokerStrategy implements PolicyGroupingStrategy {
    @Override
    public String groupKey(PolicyEntity p) {
        if (p.getBroker() == null) return "UNKNOWN_BROKER";
        return p.getBroker().getBrokerCode();
    }
}
