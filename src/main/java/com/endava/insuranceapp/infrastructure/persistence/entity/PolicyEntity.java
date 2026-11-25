package com.endava.insuranceapp.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "policies")
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", nullable = false, unique = true, length = 100)
    private String policyNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "building_id")
    private BuildingEntity building;

    @ManyToOne(optional = false)
    @JoinColumn(name = "broker_id")
    private BrokerEntity broker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_id")
    private CurrencyEntity currency;

    @Column(nullable = false, length = 20)
    private String status; // DRAFT, ACTIVE, CANCELLED

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "base_premium", precision = 19, scale = 6, nullable = false)
    private BigDecimal basePremium;

    @Column(name = "final_premium", precision = 19, scale = 6, nullable = false)
    private BigDecimal finalPremium;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;


    public Long getId() { return id; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public ClientEntity getClient() { return client; }
    public void setClient(ClientEntity client) { this.client = client; }

    public BuildingEntity getBuilding() { return building; }
    public void setBuilding(BuildingEntity building) { this.building = building; }

    public BrokerEntity getBroker() { return broker; }
    public void setBroker(BrokerEntity broker) { this.broker = broker; }

    public CurrencyEntity getCurrency() { return currency; }
    public void setCurrency(CurrencyEntity currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBasePremium() { return basePremium; }
    public void setBasePremium(BigDecimal basePremium) { this.basePremium = basePremium; }

    public BigDecimal getFinalPremium() { return finalPremium; }
    public void setFinalPremium(BigDecimal finalPremium) { this.finalPremium = finalPremium; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
