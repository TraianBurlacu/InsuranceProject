package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.RiskFactorConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskFactorConfigurationRepository extends JpaRepository<RiskFactorConfigurationEntity, Long> {
}
