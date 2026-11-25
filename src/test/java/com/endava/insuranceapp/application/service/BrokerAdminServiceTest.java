package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.infrastructure.persistence.entity.BrokerEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.BrokerRepository;
import com.endava.insuranceapp.web.dto.BrokerResponse;
import com.endava.insuranceapp.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerAdminServiceTest {

    @Mock
    private BrokerRepository repo;

    @InjectMocks
    private BrokerAdminService service;

    private BrokerEntity broker;

    @BeforeEach
    void setUp() {
        broker = new BrokerEntity();
        broker.setId(10L);
        broker.setBrokerCode("BRK001");
        broker.setName("Broker One");
        broker.setEmail("b1@endava.com");
        broker.setPhone("0712345678");
        broker.setActive(false);
        broker.setCommissionPercentage(null);
        broker.setCreatedAt(OffsetDateTime.parse("2025-01-01T10:00:00+02:00"));
        broker.setUpdatedAt(null);
    }

    @Test
    void listAll_shouldMapEntitiesToResponses() {
        BrokerEntity b1 = new BrokerEntity();
        b1.setId(1L);
        b1.setBrokerCode("B1");
        b1.setName("Name 1");
        b1.setEmail(null);
        b1.setPhone("0700000001");
        b1.setActive(true);
        b1.setCommissionPercentage(null);
        b1.setCreatedAt(OffsetDateTime.parse("2025-01-01T10:00:00+02:00"));
        b1.setUpdatedAt(null);

        BrokerEntity b2 = new BrokerEntity();
        b2.setId(2L);
        b2.setBrokerCode("B2");
        b2.setName("Name 2");
        b2.setEmail("n2@endava.com");
        b2.setPhone("0700000002");
        b2.setActive(false);
        b2.setCommissionPercentage(null);
        b2.setCreatedAt(OffsetDateTime.parse("2025-02-01T10:00:00+02:00"));
        b2.setUpdatedAt(OffsetDateTime.parse("2025-02-02T10:00:00+02:00"));

        when(repo.findAll()).thenReturn(List.of(b1, b2));

        List<BrokerResponse> result = service.listAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).brokerCode()).isEqualTo("B1");
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).email()).isEqualTo("n2@endava.com");

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getById_shouldReturnResponse_whenFound() {
        when(repo.findById(10L)).thenReturn(Optional.of(broker));

        BrokerResponse result = service.getById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.brokerCode()).isEqualTo("BRK001");
        assertThat(result.name()).isEqualTo("Broker One");
        assertThat(result.email()).isEqualTo("b1@endava.com");
        assertThat(result.phone()).isEqualTo("0712345678");
        assertThat(result.isActive()).isFalse();
        assertThat(result.createdAt()).isEqualTo(broker.getCreatedAt());
        assertThat(result.updatedAt()).isNull();

        verify(repo).findById(10L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getById_shouldThrowResourceNotFound_whenMissing() {
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker with id 10 not found");

        verify(repo).findById(10L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void activate_shouldSetActiveTrue_setUpdatedAt_save_andReturnResponse() {
        broker.setActive(false);
        broker.setUpdatedAt(null);

        when(repo.findById(10L)).thenReturn(Optional.of(broker));
        when(repo.save(any(BrokerEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        BrokerResponse result = service.activate(10L);

        // entity state changed
        assertThat(broker.isActive()).isTrue();
        assertThat(broker.getUpdatedAt()).isNotNull();

        // response reflects changes
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.isActive()).isTrue();
        assertThat(result.updatedAt()).isNotNull();

        InOrder order = inOrder(repo);
        order.verify(repo).findById(10L);
        order.verify(repo).save(broker);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void activate_shouldThrowResourceNotFound_whenMissing() {
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activate(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker with id 10 not found");

        verify(repo).findById(10L);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deactivate_shouldSetActiveFalse_setUpdatedAt_save_andReturnResponse() {
        broker.setActive(true);
        broker.setUpdatedAt(null);

        when(repo.findById(10L)).thenReturn(Optional.of(broker));
        when(repo.save(any(BrokerEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        BrokerResponse result = service.deactivate(10L);

        assertThat(broker.isActive()).isFalse();
        assertThat(broker.getUpdatedAt()).isNotNull();

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.isActive()).isFalse();
        assertThat(result.updatedAt()).isNotNull();

        InOrder order = inOrder(repo);
        order.verify(repo).findById(10L);
        order.verify(repo).save(broker);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deactivate_shouldThrowResourceNotFound_whenMissing() {
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Broker with id 10 not found");

        verify(repo).findById(10L);
        verifyNoMoreInteractions(repo);
    }
}
