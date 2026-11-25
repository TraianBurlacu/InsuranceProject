package com.endava.insuranceapp.application.service;

import com.endava.insuranceapp.application.service.report.*;
import com.endava.insuranceapp.infrastructure.persistence.entity.PolicyEntity;
import com.endava.insuranceapp.infrastructure.persistence.repository.PolicyRepository;
import com.endava.insuranceapp.web.dto.PolicyReportRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportAdminService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);


    private final PolicyRepository policyRepository;

    public ReportAdminService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<PolicyReportRow> reportByCountry(ReportFilter filter) {
        return reportByStrategy(new GroupByCountryStrategy(), filter);
    }

    public List<PolicyReportRow> reportByCounty(ReportFilter filter) {
        return reportByStrategy(new GroupByCountyStrategy(), filter);
    }

    public List<PolicyReportRow> reportByCity(ReportFilter filter) {
        return reportByStrategy(new GroupByCityStrategy(), filter);
    }

    public List<PolicyReportRow> reportByBroker(ReportFilter filter) {
        return reportByStrategy(new GroupByBrokerStrategy(), filter);
    }

    private List<PolicyReportRow> reportByStrategy(PolicyGroupingStrategy strategy, ReportFilter filter) {
        validateFilter(filter);

        LocalDate from = filter.from();
        LocalDate to = filter.to();

        List<PolicyEntity> candidates = policyRepository.findForReports(from, to);

        List<PolicyEntity> filtered = candidates.stream()
                .filter(p -> matchesStatus(p, filter.status()))
                .filter(p -> matchesCurrency(p, filter.currency()))
                .filter(p -> matchesBuildingType(p, filter.buildingType()))
                .collect(Collectors.toList());

        // group first by groupKey, then by currency
        Map<String, Map<String, List<PolicyEntity>>> grouped = filtered.stream()
                .collect(Collectors.groupingBy(
                        strategy::groupKey,
                        Collectors.groupingBy(p -> safeCurrencyCode(p.getCurrency()))
                ));

        List<PolicyReportRow> rows = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<PolicyEntity>>> g : grouped.entrySet()) {
            String groupKey = g.getKey();
            for (Map.Entry<String, List<PolicyEntity>> byCurrency : g.getValue().entrySet()) {
                String currency = byCurrency.getKey();
                List<PolicyEntity> list = byCurrency.getValue();

                long count = list.size();
                BigDecimal totalFinal = list.stream()
                        .map(PolicyEntity::getFinalPremium)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalInBase = list.stream()
                        .map(pe -> toBaseCurrency(pe.getFinalPremium(), pe.getCurrency()))
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                rows.add(new PolicyReportRow(groupKey, currency, count, totalFinal, totalInBase));
            }
        }

        rows.sort(Comparator.comparing(PolicyReportRow::groupKey).thenComparing(PolicyReportRow::currency));
        return rows;
    }

    private void validateFilter(ReportFilter filter) {
        if (filter.from() != null && filter.to() != null && filter.to().isBefore(filter.from())) {
            throw new IllegalArgumentException("from must be <= to");
        }
        if (filter.status() != null) {
            String s = filter.status().toUpperCase(Locale.ROOT);
            if (!Set.of("ACTIVE", "EXPIRED", "CANCELLED").contains(s)) {
                log.warn("Invalid status filter: {}, status must be one of ACTIVE, EXPIRED, CANCELLED", filter.status());
                throw new IllegalArgumentException("status must be one of ACTIVE, EXPIRED, CANCELLED");
            }
        }
    }

    private boolean matchesStatus(PolicyEntity p, String statusFilter) {
        if (statusFilter == null) return true;
        String f = statusFilter.toUpperCase(Locale.ROOT);
        if ("CANCELLED".equals(f)) {
            return "CANCELLED".equalsIgnoreCase(p.getStatus());
        } else if ("ACTIVE".equals(f)) {
            return "ACTIVE".equalsIgnoreCase(p.getStatus()) && !isExpired(p);
        } else if ("EXPIRED".equals(f)) {
            return isExpired(p);
        }
        return false;
    }

    private boolean isExpired(PolicyEntity p) {
        if (!"ACTIVE".equalsIgnoreCase(p.getStatus()))
            return false;
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Bucharest"));
        return p.getEndDate() != null && p.getEndDate().isBefore(today);
    }

    private boolean matchesCurrency(PolicyEntity p, String currency) {
        if (currency == null)
            return true;
        String code = safeCurrencyCode(p.getCurrency());
        return currency.equalsIgnoreCase(code);
    }

    private boolean matchesBuildingType(PolicyEntity p, String buildingType) {
        if (buildingType == null)
            return true;
        if (p.getBuilding() == null)
            return false;
        String bt = String.valueOf(p.getBuilding().getBuildingType());
        return buildingType.equalsIgnoreCase(bt);
    }

    private String safeCurrencyCode(com.endava.insuranceapp.infrastructure.persistence.entity.CurrencyEntity cur) {
        if (cur == null)
            return "UNKNOWN";
        return cur.getCode();
    }

    private BigDecimal toBaseCurrency(BigDecimal amount, com.endava.insuranceapp.infrastructure.persistence.entity.CurrencyEntity cur) {
        if (amount == null || cur == null)
            return BigDecimal.ZERO;
        BigDecimal rate;
        try {
            rate = cur.getExchangeRateToBase();
        } catch (NoSuchMethodError | RuntimeException ex) {
            rate = BigDecimal.ONE;
        }
        if (rate == null) rate = BigDecimal.ONE;
        return amount.multiply(rate);
    }
}
