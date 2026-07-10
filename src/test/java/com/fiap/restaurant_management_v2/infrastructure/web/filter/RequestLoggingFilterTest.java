package com.fiap.restaurant_management_v2.infrastructure.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter(
        new SensitiveDataMasker()
    );

    private static final FilterChain NO_OP_CHAIN = (request, response) -> {};

    @Test
    @DisplayName("Gera traceId e devolve no header X-Trace-Id da resposta")
    void generatesTraceIdAndSetsResponseHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
            "GET",
            "/api/v1/users"
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, NO_OP_CHAIN);

        String traceId = response.getHeader("X-Trace-Id");
        assertNotNull(traceId);
        assertTrue(traceId.matches("[A-Za-z0-9-]{1,64}"));
    }

    @Test
    @DisplayName("Honra X-Trace-Id entrante válido")
    void honorsIncomingTraceId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
            "GET",
            "/api/v1/users"
        );
        request.addHeader("X-Trace-Id", "abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, NO_OP_CHAIN);

        assertEquals("abc-123", response.getHeader("X-Trace-Id"));
    }

    @Test
    @DisplayName("Rejeita X-Trace-Id malformado (anti log-injection) e gera novo")
    void rejectsMalformedTraceId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
            "GET",
            "/api/v1/users"
        );
        request.addHeader("X-Trace-Id", "abc\ndef {injection}");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, NO_OP_CHAIN);

        String traceId = response.getHeader("X-Trace-Id");
        assertNotEquals("abc\ndef {injection}", traceId);
        assertTrue(traceId.matches("[A-Za-z0-9-]{1,64}"));
    }

    @Test
    @DisplayName("traceId fica no MDC durante a cadeia e some depois")
    void mdcSetDuringChainAndClearedAfter() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
            "GET",
            "/api/v1/users"
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> mdcDuringChain = new AtomicReference<>();
        FilterChain capturingChain = (req, res) ->
            mdcDuringChain.set(MDC.get("traceId"));

        filter.doFilter(request, response, capturingChain);

        assertNotNull(mdcDuringChain.get());
        assertNull(MDC.get("traceId"));
    }

    @Test
    @DisplayName("Corpo da resposta é preservado após o wrap de caching")
    void preservesResponseBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(
            "POST",
            "/api/v1/users"
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain writingChain = (req, res) -> {
            HttpServletResponse httpResponse = (HttpServletResponse) res;
            httpResponse.setStatus(201);
            httpResponse.getWriter().write("{\"id\":\"1\"}");
        };

        filter.doFilter(request, response, writingChain);

        assertEquals(201, response.getStatus());
        assertEquals("{\"id\":\"1\"}", response.getContentAsString());
    }
}
