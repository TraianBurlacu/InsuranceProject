package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<CurrencyEntity> findByCodeIgnoreCase(String code);

    Optional<CurrencyEntity> findByCodeIgnoreCaseAndActiveTrue(String code);

    List<CurrencyEntity> findAllByActiveTrue();
}
