package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.*;
import com.endava.insuranceapp.infrastructure.persistence.repository.*;
import com.endava.insuranceapp.web.dto.PolicyCreateRequest;
import com.endava.insuranceapp.web.dto.PolicyResponse;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyServiceTest {

    private PolicyRepository policyRepo;
    private ClientRepository clientRepo;
    private BuildingRepository buildingRepo;
    private BrokerRepository brokerRepo;
    private CurrencyRepository currencyRepo;
    private PolicyCalculationService calcService;
    private CurrencyConversionService conversionService;

    private PolicyService service;

    @BeforeEach
    void setUp() {
        policyRepo = mock(PolicyRepository.class);
        clientRepo = mock(ClientRepository.class);
        buildingRepo = mock(BuildingRepository.class);
        brokerRepo = mock(BrokerRepository.class);
        currencyRepo = mock(CurrencyRepository.class);
        calcService = mock(PolicyCalculationService.class);
        //conversionService = mock(CurrencyConversionService.class);

        service = new PolicyService(policyRepo, clientRepo, buildingRepo, brokerRepo, currencyRepo, calcService, conversionService);
    }

    @Test
    void createDraft_success() {
        long clientId = 1L;
        long buildingId = 10L;
        String brokerCode = "BRK-001";
        String currencyCode = "RON";

        ClientEntity client = new ClientEntity();
        ClientEntity clientMock = mock(ClientEntity.class);
        when(clientMock.getId()).thenReturn(clientId);

        BuildingEntity buildingMock = mock(BuildingEntity.class);
        ClientEntity buildingClientMock = mock(ClientEntity.class);
        when(buildingClientMock.getId()).thenReturn(clientId);
        when(buildingMock.getClient()).thenReturn(buildingClientMock);

        BrokerEntity brokerMock = mock(BrokerEntity.class);
        when(brokerMock.isActive()).thenReturn(true);
        when(brokerMock.getId()).thenReturn(5L);
        when(brokerMock.getBrokerCode()).thenReturn(brokerCode);

        CurrencyEntity currencyMock = mock(CurrencyEntity.class);
        when(currencyMock.getCode()).thenReturn(currencyCode);

        when(clientRepo.findById(clientId)).thenReturn(Optional.of(clientMock));
        when(buildingRepo.findById(buildingId)).thenReturn(Optional.of(buildingMock));
        when(brokerRepo.findByBrokerCodeIgnoreCaseAndActiveTrue(brokerCode)).thenReturn(Optional.of(brokerMock));
        when(currencyRepo.findByCodeIgnoreCase(currencyCode)).thenReturn(Optional.of(currencyMock));

        BigDecimal base = new BigDecimal("1000");
        BigDecimal finalPremium = new BigDecimal("1100");
        when(calcService.computeFinalPremium(eq(base), any(LocalDate.class), eq(buildingMock), eq(brokerMock)))
                .thenReturn(finalPremium);

        ArgumentCaptor<PolicyEntity> captor = ArgumentCaptor.forClass(PolicyEntity.class);
        when(policyRepo.save(captor.capture())).thenAnswer(inv -> {
            PolicyEntity p = inv.getArgument(0);
            return p;
        });

        PolicyCreateRequest req = new PolicyCreateRequest(
                clientId,
                buildingId,
                brokerCode,
                currencyCode,
                base,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        PolicyResponse resp = service.createDraft(req);

        PolicyEntity saved = captor.getValue();
        assertEquals("DRAFT", saved.getStatus());
        assertEquals(base, saved.getBasePremium());
        assertEquals(finalPremium, saved.getFinalPremium());
        assertSame(brokerMock, saved.getBroker());
        assertSame(currencyMock, saved.getCurrency());

        assertEquals("DRAFT", resp.status());
        assertEquals(currencyCode, resp.currencyCode());
        assertEquals(brokerCode, resp.brokerCode());
    }

    @Test
    void createDraft_clientMissing_throws404() {
        when(clientRepo.findById(1L)).thenReturn(Optional.empty());

        PolicyCreateRequest req = new PolicyCreateRequest(
                1L, 10L, "BRK-001", "RON",
                new BigDecimal("1000"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        assertThrows(ResourceNotFoundException.class, () -> service.createDraft(req));
        verify(policyRepo, never()).save(any());
    }

    @Test
    void createDraft_buildingMissing_throws404() {
        ClientEntity client = mock(ClientEntity.class);
        when(client.getId()).thenReturn(1L);
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepo.findById(10L)).thenReturn(Optional.empty());

        PolicyCreateRequest req = new PolicyCreateRequest(
                1L, 10L, "BRK-001", "RON",
                new BigDecimal("1000"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        assertThrows(ResourceNotFoundException.class, () -> service.createDraft(req));
        verify(policyRepo, never()).save(any());
    }

    @Test
    void createDraft_buildingNotOwnedByClient_throws400() {
        ClientEntity client = mock(ClientEntity.class);
        when(client.getId()).thenReturn(1L);

        BuildingEntity building = mock(BuildingEntity.class);
        ClientEntity otherClient = mock(ClientEntity.class);
        when(otherClient.getId()).thenReturn(999L);
        when(building.getClient()).thenReturn(otherClient);

        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepo.findById(10L)).thenReturn(Optional.of(building));

        PolicyCreateRequest req = new PolicyCreateRequest(
                1L, 10L, "BRK-001", "RON",
                new BigDecimal("1000"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createDraft(req));
        assertTrue(ex.getMessage().toLowerCase().contains("does not belong"));
        verify(policyRepo, never()).save(any());
    }

    @Test
    void createDraft_brokerMissing_throws404() {
        ClientEntity client = mock(ClientEntity.class);
        when(client.getId()).thenReturn(1L);

        BuildingEntity building = mock(BuildingEntity.class);
        ClientEntity buildingClient = mock(ClientEntity.class);
        when(buildingClient.getId()).thenReturn(1L);
        when(building.getClient()).thenReturn(buildingClient);

        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepo.findById(10L)).thenReturn(Optional.of(building));

        when(brokerRepo.findByBrokerCodeIgnoreCaseAndActiveTrue("BRK-001"))
                .thenReturn(Optional.empty());
        when(brokerRepo.findByBrokerCodeIgnoreCase("BRK-001"))
                .thenReturn(Optional.empty()); // so it's truly missing => 404

        PolicyCreateRequest req = new PolicyCreateRequest(
                1L, 10L, "BRK-001", "RON",
                new BigDecimal("1000"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        assertThrows(ResourceNotFoundException.class, () -> service.createDraft(req));
        verify(policyRepo, never()).save(any());
    }

    @Test
    void createDraft_brokerInactive_throws400() {
        ClientEntity client = mock(ClientEntity.class);
        when(client.getId()).thenReturn(1L);

        BuildingEntity building = mock(BuildingEntity.class);
        ClientEntity buildingClient = mock(ClientEntity.class);
        when(buildingClient.getId()).thenReturn(1L);
        when(building.getClient()).thenReturn(buildingClient);

        BrokerEntity inactiveBroker = mock(BrokerEntity.class);
        when(inactiveBroker.isActive()).thenReturn(false);

        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(buildingRepo.findById(10L)).thenReturn(Optional.of(building));

        when(brokerRepo.findByBrokerCodeIgnoreCaseAndActiveTrue("BRK-001"))
                .thenReturn(Optional.empty());
        when(brokerRepo.findByBrokerCodeIgnoreCase("BRK-001"))
                .thenReturn(Optional.of(inactiveBroker)); // exists but inactive => 400

        PolicyCreateRequest req = new PolicyCreateRequest(
                1L, 10L, "BRK-001", "RON",
                new BigDecimal("1000"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10)
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createDraft(req));
        assertTrue(ex.getMessage().toLowerCase().contains("inactive"));
        verify(policyRepo, never()).save(any());
    }

    @Test
    void activate_success_changesStatusToActive() {
        PolicyEntity policy = new PolicyEntity();
        policy.setStatus("DRAFT");
        policy.setStartDate(LocalDate.now().plusDays(1));

        ClientEntity client = mock(ClientEntity.class); when(client.getId()).thenReturn(1L);
        BuildingEntity building = mock(BuildingEntity.class); when(building.getId()).thenReturn(10L);
        BrokerEntity broker = mock(BrokerEntity.class); when(broker.getId()).thenReturn(5L); when(broker.getBrokerCode()).thenReturn("BRK-001");
        CurrencyEntity currency = mock(CurrencyEntity.class); when(currency.getCode()).thenReturn("RON");

        policy.setClient(client);
        policy.setBuilding(building);
        policy.setBroker(broker);
        policy.setCurrency(currency);
        policy.setBasePremium(new BigDecimal("1000"));
        policy.setFinalPremium(new BigDecimal("1100"));
        policy.setEndDate(LocalDate.now().plusDays(10));

        when(policyRepo.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PolicyResponse resp = service.activate(1L);

        assertEquals("ACTIVE", policy.getStatus());
        assertEquals("ACTIVE", resp.status());
    }

    @Test
    void activate_startDateInPast_throws400() {
        PolicyEntity policy = new PolicyEntity();
        policy.setStatus("DRAFT");
        policy.setStartDate(LocalDate.now().minusDays(1));
        when(policyRepo.findById(1L)).thenReturn(Optional.of(policy));

        assertThrows(IllegalArgumentException.class, () -> service.activate(1L));
        verify(policyRepo, never()).save(any());
    }

    @Test
    void cancel_success_changesStatusToCancelled() {
        PolicyEntity policy = new PolicyEntity();
        policy.setStatus("ACTIVE");
        policy.setStartDate(LocalDate.now().plusDays(1));

        ClientEntity client = mock(ClientEntity.class); when(client.getId()).thenReturn(1L);
        BuildingEntity building = mock(BuildingEntity.class); when(building.getId()).thenReturn(10L);
        BrokerEntity broker = mock(BrokerEntity.class); when(broker.getId()).thenReturn(5L); when(broker.getBrokerCode()).thenReturn("BRK-001");
        CurrencyEntity currency = mock(CurrencyEntity.class); when(currency.getCode()).thenReturn("RON");

        policy.setClient(client);
        policy.setBuilding(building);
        policy.setBroker(broker);
        policy.setCurrency(currency);
        policy.setBasePremium(new BigDecimal("1000"));
        policy.setFinalPremium(new BigDecimal("1100"));
        policy.setEndDate(LocalDate.now().plusDays(10));

        when(policyRepo.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PolicyResponse resp = service.cancel(1L, "Client requested");

        assertEquals("CANCELLED", policy.getStatus());
        assertEquals("Client requested", policy.getCancellationReason());
        assertEquals("CANCELLED", resp.status());
    }
}
