package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.FeeConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeConfigurationRepository extends JpaRepository<FeeConfigurationEntity, Long> {
}
