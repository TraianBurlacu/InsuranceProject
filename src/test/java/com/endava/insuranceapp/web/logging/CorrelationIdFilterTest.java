package com.endava.insuranceapp.web.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    @AfterEach
    void cleanup() {
        MDC.remove(CorrelationIdFilter.MDC_KEY);
    }

    @Test
    void whenHeaderMissing_generatesTraceId_putsInResponse_andMdc() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter();

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
        MockHttpServletResponse res = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        String header = res.getHeader(CorrelationIdFilter.HEADER);
        assertNotNull(header);
        assertFalse(header.isBlank());

        // MDC should be cleaned after request
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));

        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    void whenHeaderProvided_reusesTraceId() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter();

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
        req.addHeader(CorrelationIdFilter.HEADER, "TRACE-123");
        MockHttpServletResponse res = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        assertEquals("TRACE-123", res.getHeader(CorrelationIdFilter.HEADER));
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
        verify(chain, times(1)).doFilter(any(), any());
    }
}
