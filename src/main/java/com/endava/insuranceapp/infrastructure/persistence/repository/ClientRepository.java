package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    boolean existsByIdentifier(String identifier);

    Optional<ClientEntity> findByIdentifier(String identifier);

    Page<ClientEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
