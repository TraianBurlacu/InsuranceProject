package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.ClientEntity;
import com.endava.insuranceapp.infrastructure.persistence.entity.ClientType;
import com.endava.insuranceapp.infrastructure.persistence.repository.ClientRepository;
import com.endava.insuranceapp.web.dto.ClientCreateRequest;
import com.endava.insuranceapp.web.dto.ClientUpdateRequest;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceValidationTest {

    private ClientRepository clientRepository;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        clientService = new ClientService(clientRepository);
    }

    @Test
    void create_whenIdentifierExists_throwsIllegalArgumentException() {
        when(clientRepository.existsByIdentifier("DUP_ID")).thenReturn(true);

        ClientCreateRequest req = new ClientCreateRequest(
                "INDIVIDUAL",
                "Jane Doe",
                "DUP_ID",
                "jane@doe.com",
                "0712345678",
                "Some address",
                null
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> clientService.create(req));
        assertTrue(ex.getMessage().toLowerCase().contains("identifier"));
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    void getById_whenMissing_throwsResourceNotFoundException() {
        when(clientRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.getById(42L));
        verify(clientRepository).findById(42L);
    }

    @Test
    void create_whenTypeInvalid_throwsIllegalArgumentException() {
        when(clientRepository.existsByIdentifier("ID_X")).thenReturn(false);

        ClientCreateRequest req = new ClientCreateRequest(
                "UNKNOWN_TYPE",
                "Some Name",
                "ID_X",
                "foo@bar.com",
                "0700",
                "Addr",
                null
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> clientService.create(req));
        assertTrue(ex.getMessage().toLowerCase().contains("type"));
        verify(clientRepository, never()).save(any(ClientEntity.class));
    }
}
