package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrokerRepository extends JpaRepository<BrokerEntity, Long> {

    boolean existsByBrokerCodeIgnoreCase(String brokerCode);

    Optional<BrokerEntity> findByBrokerCodeIgnoreCase(String brokerCode);

    boolean existsByBrokerCodeIgnoreCaseAndActiveTrue(String brokerCode);

    Optional<BrokerEntity> findByBrokerCodeIgnoreCaseAndActiveTrue(String brokerCode);
}
