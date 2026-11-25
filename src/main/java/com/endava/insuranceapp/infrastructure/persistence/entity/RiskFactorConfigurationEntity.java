package com.endava.insuranceapp.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "risk_factor_configurations")
public class RiskFactorConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String level; // Country / County / City / BuildingType

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "building_type", length = 30)
    private String buildingType;

    @Column(name = "adjustment_percentage", nullable = false, precision = 19, scale = 6)
    private BigDecimal adjustmentPercentage;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Long getId() { return id; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }

    public BigDecimal getAdjustmentPercentage() { return adjustmentPercentage; }
    public void setAdjustmentPercentage(BigDecimal adjustmentPercentage) { this.adjustmentPercentage = adjustmentPercentage; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
