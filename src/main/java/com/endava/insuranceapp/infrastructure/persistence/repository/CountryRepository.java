package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
}
