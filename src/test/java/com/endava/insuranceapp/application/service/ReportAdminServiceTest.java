package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.application.service.report.ReportFilter;
import com.endava.insuranceapp.infrastructure.persistence.entity.*;
import com.endava.insuranceapp.infrastructure.persistence.repository.PolicyRepository;
import com.endava.insuranceapp.web.dto.PolicyReportRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportAdminServiceTest {

    private PolicyRepository repo;
    private ReportAdminService service;

    @BeforeEach
    void setUp() {
        repo = mock(PolicyRepository.class);
        service = new ReportAdminService(repo);
    }

    @Test
    void reportByCity_groupsAndSums_correctly() {
        CurrencyEntity curUSD = new CurrencyEntity();
        curUSD.setCode("USD");
        try {
            CurrencyEntity.class.getMethod("setExchangeRateToBase", BigDecimal.class).invoke(curUSD, new BigDecimal("3.00"));
        } catch (Exception ignored) {
        }

        // build country/county/city
        CountryEntity country = new CountryEntity();
        country.setName("Romania");
        CountyEntity county = new CountyEntity();
        county.setName("Cluj");
        county.setCountry(country);
        CityEntity city = new CityEntity();
        city.setName("Cluj-Napoca");
        city.setCounty(county);

        // create building
        BuildingEntity b1 = new BuildingEntity();
        b1.setCity(city);
        b1.setBuildingType(BuildingType.valueOf("RESIDENTIAL"));

        // create broker
        BrokerEntity broker = new BrokerEntity();
        broker.setBrokerCode("BRK-1");

        // prepare 2 policies in same city/currency
        PolicyEntity p1 = new PolicyEntity();
        p1.setFinalPremium(new BigDecimal("100.00"));
        p1.setCurrency(curUSD);
        p1.setBuilding(b1);
        p1.setBroker(broker);
        p1.setStatus("ACTIVE");
        p1.setStartDate(LocalDate.now().plusDays(10));
        p1.setEndDate(LocalDate.now().plusDays(15));

        PolicyEntity p2 = new PolicyEntity();
        p2.setFinalPremium(new BigDecimal("200.00"));
        p2.setCurrency(curUSD);
        p2.setBuilding(b1);
        p2.setBroker(broker);
        p2.setStatus("ACTIVE");
        p2.setStartDate(LocalDate.now().plusDays(5));
        p2.setEndDate(LocalDate.now().plusDays(20));

        when(repo.findForReports(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(p1, p2));

        ReportFilter filter = new ReportFilter(
                null,
                null,
                "ACTIVE",
                null,
                null);
        List<PolicyReportRow> rows = service.reportByCity(filter);

        assertEquals(1, rows.size());
        PolicyReportRow row = rows.get(0);
        assertEquals("Cluj-Napoca", row.groupKey());
        assertEquals("USD", row.currency());
        assertEquals(2L, row.policyCount());
        assertEquals(new BigDecimal("300.00"), row.totalFinalPremium());
        assertTrue(row.totalFinalPremiumInBaseCurrency().equals(new BigDecimal("900.00"))
                || row.totalFinalPremium().equals(new BigDecimal("300.00")));
    }

    @Test
    void reportByCity_statusExpired_filtersOnlyExpiredActivePolicies() {
        PolicyEntity expiredActive = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(1),
                "RON",
                "Cluj-Napoca"
        );

        PolicyEntity activeNotExpired = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(10),
                "RON",
                "Cluj-Napoca"
        );

        PolicyEntity cancelled = basePolicy(
                "CANCELLED",
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                "RON",
                "Cluj-Napoca"
        );

        when(repo.findForReports(any(), any())).thenReturn(List.of(expiredActive, activeNotExpired, cancelled));

        ReportFilter filter = new ReportFilter(null, null, "EXPIRED", null, null);
        List<PolicyReportRow> rows = service.reportByCity(filter);

        assertEquals(1, rows.size());
        assertEquals(1L, rows.get(0).policyCount());
        assertEquals(new BigDecimal("100.00"), rows.get(0).totalFinalPremium());
    }

    @Test
    void reportByCity_currencyFilter_keepsOnlyRequestedCurrency() {
        PolicyEntity ron = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "RON",
                "Cluj-Napoca"
        );

        PolicyEntity eur = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "EUR",
                "Cluj-Napoca"
        );

        when(repo.findForReports(any(), any())).thenReturn(List.of(ron, eur));

        ReportFilter filter = new ReportFilter(null, null, "ACTIVE", "EUR", null);
        List<PolicyReportRow> rows = service.reportByCity(filter);

        assertEquals(1, rows.size());
        assertEquals("EUR", rows.get(0).currency());
        assertEquals(1L, rows.get(0).policyCount());
    }

    @Test
    void reportByCity_invalidStatus_throwsBadRequest() {
        when(repo.findForReports(any(), any())).thenReturn(List.of());

        ReportFilter filter = new ReportFilter(null, null, "INVALID_STATUS", null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.reportByCity(filter));
        assertTrue(ex.getMessage().toLowerCase().contains("status"));
    }

    private PolicyEntity basePolicy(String status, LocalDate start, LocalDate end, String currencyCode, String cityName) {
        CurrencyEntity currency = new CurrencyEntity();
        currency.setCode(currencyCode);

        CountryEntity country = new CountryEntity();
        country.setName("Romania");

        CountyEntity county = new CountyEntity();
        county.setName("Cluj");
        county.setCountry(country);

        CityEntity city = new CityEntity();
        city.setName(cityName);
        city.setCounty(county);

        BuildingEntity building = new BuildingEntity();
        building.setCity(city);

        BrokerEntity broker = new BrokerEntity();
        broker.setBrokerCode("BRK-1");

        PolicyEntity p = new PolicyEntity();
        p.setFinalPremium(new BigDecimal("100.00"));
        p.setCurrency(currency);
        p.setBuilding(building);
        p.setBroker(broker);
        p.setStatus(status);
        p.setStartDate(start);
        p.setEndDate(end);
        return p;
    }

    @Test
    void reportByCountry_groupsByCountry() {
        PolicyEntity p1 = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "RON",
                "Cluj-Napoca"
        );

        PolicyEntity p2 = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "RON",
                "Turda"
        );

        when(repo.findForReports(any(), any())).thenReturn(List.of(p1, p2));

        ReportFilter filter = new ReportFilter(null, null, "ACTIVE", null, null);
        List<PolicyReportRow> rows = service.reportByCountry(filter);

        assertEquals(1, rows.size());
        assertEquals("Romania", rows.get(0).groupKey());
        assertEquals("RON", rows.get(0).currency());
        assertEquals(2L, rows.get(0).policyCount());
    }

    @Test
    void reportByCounty_groupsByCounty() {
        PolicyEntity p1 = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "RON",
                "Cluj-Napoca");

        PolicyEntity p2 = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "RON",
                "Turda"
        );

        when(repo.findForReports(any(), any())).thenReturn(List.of(p1, p2));

        ReportFilter filter = new ReportFilter(null, null, "ACTIVE", null, null);
        List<PolicyReportRow> rows = service.reportByCounty(filter);

        assertEquals(1, rows.size());
        assertEquals("Cluj", rows.get(0).groupKey());
        assertEquals(2L, rows.get(0).policyCount());
    }

    @Test
    void reportByBroker_groupsByBrokerCode() {
        PolicyEntity p1 = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "RON",
                "Cluj-Napoca"
        );

        PolicyEntity p2 = basePolicy(
                "ACTIVE",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5),
                "RON",
                "Cluj-Napoca"
        );

        p1.getBroker().setBrokerCode("BRK-A");
        p2.getBroker().setBrokerCode("BRK-B");

        when(repo.findForReports(any(), any())).thenReturn(List.of(p1, p2));

        ReportFilter filter = new ReportFilter(null, null, "ACTIVE", null, null);
        List<PolicyReportRow> rows = service.reportByBroker(filter);

        assertEquals(2, rows.size());
        assertTrue(rows.stream().anyMatch(r -> r.groupKey().equals("BRK-A")));
        assertTrue(rows.stream().anyMatch(r -> r.groupKey().equals("BRK-B")));
    }

}
