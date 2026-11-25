package com.endava.insuranceapp.application.service.report;

import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;

public interface PolicyGroupingStrategy {
    String groupKey(PolicyEntity p);
}
