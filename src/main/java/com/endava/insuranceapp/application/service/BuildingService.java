package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.*;
import com.endava.insuranceapp.infrastructure.persistence.repository.BuildingRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.CityRepository;
import com.endava.insuranceapp.infrastructure.persistence.repository.ClientRepository;
import com.endava.insuranceapp.web.dto.BuildingCreateRequest;
import com.endava.insuranceapp.web.dto.BuildingResponse;
import com.endava.insuranceapp.web.dto.BuildingUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class BuildingService {

    private static final Logger log = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final ClientRepository clientRepository;
    private final CityRepository cityRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           ClientRepository clientRepository,
                           CityRepository cityRepository) {
        this.buildingRepository = buildingRepository;
        this.clientRepository = clientRepository;
        this.cityRepository = cityRepository;
    }

    @Transactional
    public BuildingResponse create(Long clientId, BuildingCreateRequest request) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id " + clientId + " not found"));

        CityEntity city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("City with id " + request.cityId() + " not found"));

        BuildingEntity entity = new BuildingEntity();
        entity.setClient(client);
        apply(entity, city, request.street(), request.number(), request.constructionYear(),
                request.buildingType(), request.floors(), request.surfaceArea(), request.insuredValue(),
                request.riskIndicators());

        log.info("Created building buildingId={} for clientId={}", entity.getId(), clientId);
        return toResponse(buildingRepository.save(entity));
    }

    @Transactional
    public BuildingResponse update(Long buildingId, BuildingUpdateRequest request) {
        BuildingEntity entity = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building with id " + buildingId + " not found"));

        CityEntity city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("City with id " + request.cityId() + " not found"));

        apply(entity, city, request.street(), request.number(), request.constructionYear(),
                request.buildingType(), request.floors(), request.surfaceArea(), request.insuredValue(),
                request.riskIndicators());

        log.info("Updated building buildingId={} for clientId={}", entity.getId(), entity.getId());
        return toResponse(buildingRepository.save(entity));
    }

    @Transactional
    public BuildingResponse getById(Long buildingId) {
        BuildingEntity entity = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building with id " + buildingId + " not found"));
        return toResponse(entity);
    }

    @Transactional
    public List<BuildingResponse> listByClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            log.warn("Client with id {} not found when listing buildings", clientId);
            throw new ResourceNotFoundException("Client with id " + clientId + " not found");
        }
        return buildingRepository.findByClientIdOrderByIdAsc(clientId).stream()
                .map(this::toResponse)
                .toList();
    }

    private void apply(BuildingEntity entity,
                       CityEntity city,
                       String street,
                       String number,
                       Integer constructionYear,
                       String buildingType,
                       Integer floors,
                       java.math.BigDecimal surfaceArea,
                       java.math.BigDecimal insuredValue,
                       String riskIndicators) {

        entity.setCity(city);
        entity.setStreet(street.trim());
        entity.setNumber(number.trim());
        entity.setConstructionYear(constructionYear);
        entity.setBuildingType(parseBuildingType(buildingType));
        entity.setFloors(floors);
        entity.setSurfaceArea(surfaceArea);
        entity.setInsuredValue(insuredValue);
        entity.setRiskIndicators(riskIndicators);
    }

    private BuildingType parseBuildingType(String type) {
        try {
            return BuildingType.valueOf(type.trim().toUpperCase());
        } catch (Exception ex) {
            log.warn("Invalid buildingType value: {}", type);
            throw new IllegalArgumentException("buildingType must be RESIDENTIAL, OFFICE, or INDUSTRIAL");
        }
    }

    private BuildingResponse toResponse(BuildingEntity e) {
        CityEntity city = e.getCity();
        CountyEntity county = city.getCounty();
        CountryEntity country = county.getCountry();

        return new BuildingResponse(
                e.getId(),
                e.getClient().getId(),
                city.getId(),
                city.getName(),
                county.getName(),
                country.getName(),
                e.getStreet(),
                e.getNumber(),
                e.getConstructionYear(),
                e.getBuildingType().name(),
                e.getFloors(),
                e.getSurfaceArea(),
                e.getInsuredValue(),
                e.getRiskIndicators()
        );
    }
}
