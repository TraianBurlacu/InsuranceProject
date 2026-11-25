package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<AdministratorEntity, Long> {

    Optional<AdministratorEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
