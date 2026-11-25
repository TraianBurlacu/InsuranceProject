package com.endava.insuranceapp.infrastructure.persistence.repository;

import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PolicyRepository extends JpaRepository<PolicyEntity, Long> {

    @Query("select p from PolicyEntity p where "
            + "(:clientId is null or p.client.id = :clientId) and "
            + "(:brokerId is null or p.broker.id = :brokerId) and "
            + "(:status is null or p.status = :status) and "
            + "(:startDate is null or p.startDate >= :startDate) and "
            + "(:endDate is null or p.endDate <= :endDate)")
    List<PolicyEntity> search(
            @Param("clientId") Long clientId,
            @Param("brokerId") Long brokerId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        select p from PolicyEntity p
          join fetch p.currency cur
          join fetch p.broker b
          join fetch p.building bu
          join fetch bu.city ci
          join fetch ci.county co
          join fetch co.country cou
        where (:from is null or p.startDate >= :from)
          and (:to is null or p.startDate <= :to)
    """)
    List<PolicyEntity> findForReports(@Param("from") LocalDate from,
                                      @Param("to") LocalDate to);

}
