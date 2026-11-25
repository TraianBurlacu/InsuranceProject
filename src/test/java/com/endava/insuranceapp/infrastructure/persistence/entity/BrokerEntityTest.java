package com.endava.insuranceapp.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BrokerEntityTest {

    @Test
    void gettersAndSetters_work() {
        BrokerEntity e = new BrokerEntity();

        e.setId(1L);
        e.setBrokerCode("BRK-001");
        e.setName("Broker One");
        e.setEmail("b1@test.com");
        e.setPhone("0711111111");
        e.setActive(true);
        e.setCommissionPercentage(new BigDecimal("0.05"));

        OffsetDateTime now = OffsetDateTime.now();
        e.setCreatedAt(now);
        e.setUpdatedAt(now.plusMinutes(1));

        assertEquals(1L, e.getId());
        assertEquals("BRK-001", e.getBrokerCode());
        assertEquals("Broker One", e.getName());
        assertEquals("b1@test.com", e.getEmail());
        assertEquals("0711111111", e.getPhone());
        assertTrue(e.isActive());
        assertEquals(new BigDecimal("0.05"), e.getCommissionPercentage());
        assertEquals(now, e.getCreatedAt());
        assertEquals(now.plusMinutes(1), e.getUpdatedAt());
    }

    @Test
    void defaultActive_isFalseUnlessSet() {
        BrokerEntity e = new BrokerEntity();
        assertFalse(e.isActive());
    }
}
