package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.ClientEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.ClientRepository;
import com.endava.insuranceapp.web.dto.ClientCreateRequest;
import com.endava.insuranceapp.web.dto.ClientUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    ClientRepository clientRepository;
    ClientService clientService;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        clientService = new ClientService(clientRepository);
    }

    @Test
    void create_whenIdentifierExists_throwsIllegalArgumentException() {
        when(clientRepository.existsByIdentifier("ABC")).thenReturn(true);

        var req = new ClientCreateRequest(
                "INDIVIDUAL",
                "John",
                "ABC",
                "john@doe.com",
                "07",
                "addr",
                null);
        assertThrows(IllegalArgumentException.class, () -> clientService.create(req));

        verify(clientRepository, never()).save(any());
    }

    @Test
    void getById_whenMissing_throwsNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> clientService.getById(1L));
    }

    @Test
    void update_whenExists_updatesFields() {
        ClientEntity existing = new ClientEntity();
        existing.setName("Old");
        existing.setIdentifier("ID1");
        existing.setType(com.endava.insuranceapp.infrastructure.persistence.entity.ClientType.INDIVIDUAL);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(ClientEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        var req = new ClientUpdateRequest(
                "New Name",
                "new@mail.com",
                "0700",
                "New Address",
                null);
        var resp = clientService.update(1L, req);

        assertEquals("New Name", resp.name());
        assertEquals("new@mail.com", resp.email());
    }
}
