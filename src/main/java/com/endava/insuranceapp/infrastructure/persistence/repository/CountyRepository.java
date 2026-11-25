package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.CountyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountyRepository extends JpaRepository<CountyEntity, Long> {
    List<CountyEntity> findByCountryIdOrderByNameAsc(Long countryId);
}
