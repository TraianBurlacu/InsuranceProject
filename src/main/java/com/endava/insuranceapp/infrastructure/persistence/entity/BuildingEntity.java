package com.endava.insuranceapp.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "buildings")
public class BuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(nullable = false, length = 20)
    private String number;

    @Column(name = "construction_year")
    private Integer constructionYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type", nullable = false, length = 30)
    private BuildingType buildingType;

    private Integer floors;

    @Column(name = "surface_area", nullable = false, precision = 12, scale = 2)
    private BigDecimal surfaceArea;

    @Column(name = "insured_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal insuredValue;

    @Column(name = "risk_indicators", length = 500)
    private String riskIndicators;

    public Long getId() { return id; }

    public ClientEntity getClient() { return client; }
    public void setClient(ClientEntity client) { this.client = client; }

    public CityEntity getCity() { return city; }
    public void setCity(CityEntity city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public Integer getConstructionYear() { return constructionYear; }
    public void setConstructionYear(Integer constructionYear) { this.constructionYear = constructionYear; }

    public BuildingType getBuildingType() { return buildingType; }
    public void setBuildingType(BuildingType buildingType) { this.buildingType = buildingType; }

    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }

    public BigDecimal getSurfaceArea() { return surfaceArea; }
    public void setSurfaceArea(BigDecimal surfaceArea) { this.surfaceArea = surfaceArea; }

    public BigDecimal getInsuredValue() { return insuredValue; }
    public void setInsuredValue(BigDecimal insuredValue) { this.insuredValue = insuredValue; }

    public String getRiskIndicators() { return riskIndicators; }
    public void setRiskIndicators(String riskIndicators) { this.riskIndicators = riskIndicators; }
}
