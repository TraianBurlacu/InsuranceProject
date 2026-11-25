package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.ClientEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.ClientType;
import com.endava.insuranceapp.infrastructure.persistence.repository.ClientRepository;
import com.endava.insuranceapp.web.dto.ClientCreateRequest;
import com.endava.insuranceapp.web.dto.ClientResponse;
import com.endava.insuranceapp.web.dto.ClientUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientResponse create(ClientCreateRequest request) {
        String identifier = request.identifier().trim();

        if (clientRepository.existsByIdentifier(identifier)) {
            log.warn("Client with identifier {} already exists", identifier);
            throw new IllegalArgumentException("identifier already exists");
        }

        ClientEntity entity = new ClientEntity();
        entity.setType(parseType(request.type()));
        entity.setName(request.name().trim());
        entity.setIdentifier(identifier);
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setAddress(request.address());
        entity.setPrimaryAddress(request.primaryAddress() == null ? null : request.primaryAddress().trim());

        log.info("Created client {}, with id = {}", entity.getName(), entity.getId());
        return toResponse(clientRepository.save(entity));
    }

    public ClientResponse update(Long id, ClientUpdateRequest request) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id " + id + " not found"));

        entity.setName(request.name().trim());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setAddress(request.address());
        entity.setPrimaryAddress(request.primaryAddress() == null ? null : request.primaryAddress().trim());

        log.info("Updated client {}, with id = {}", entity.getName(), entity.getId());
        return toResponse(clientRepository.save(entity));
    }

    public ClientResponse getById(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id " + id + " not found"));
        return toResponse(entity);
    }

    public Page<ClientResponse> search(String name, String identifier, Pageable pageable) {
        if (identifier != null && !identifier.isBlank()) {
            return clientRepository.findByIdentifier(identifier.trim())
                    .map(this::toResponse)
                    .map(r -> new PageImpl<>(List.of(r), pageable, 1))
                    .orElse(new PageImpl<>(List.of(), pageable, 0));
        }

        String safeName = (name == null) ? "" : name.trim();
        return clientRepository.findByNameContainingIgnoreCase(safeName, pageable)
                .map(this::toResponse);
    }

    private ClientType parseType(String type) {
        try {
            return ClientType.valueOf(type.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("type must be INDIVIDUAL or COMPANY");
        }
    }

    private ClientResponse toResponse(ClientEntity e) {
        return new ClientResponse(
                e.getId(),
                e.getType().name(),
                e.getName(),
                e.getIdentifier(),
                e.getEmail(),
                e.getPhone(),
                e.getAddress(),
                e.getPrimaryAddress()
        );
    }
}
