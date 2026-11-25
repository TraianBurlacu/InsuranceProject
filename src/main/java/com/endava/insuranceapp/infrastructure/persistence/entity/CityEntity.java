package com.endava.insuranceapp.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "county_id", nullable = false)
    private CountyEntity county;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CountyEntity getCounty() { return county; }
    public void setCounty(CountyEntity county) { this.county = county; }
}
