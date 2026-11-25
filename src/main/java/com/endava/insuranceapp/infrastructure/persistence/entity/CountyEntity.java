package com.endava.insuranceapp.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "counties")
public class CountyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @OneToMany(mappedBy = "county", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CityEntity> cities = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CountryEntity getCountry() { return country; }
    public void setCountry(CountryEntity country) { this.country = country; }

    public List<CityEntity> getCities() { return cities; }
}
