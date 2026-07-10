package com.fiap.restaurant_management_v2.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RequestLoggingFilter.class
    );
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";
    private static final String TRACE_ID_PATTERN = "[A-Za-z0-9-]{1,64}";
    private static final int MAX_BODY_LOG_LENGTH = 1000;
    // cache maior que o log-cap: o masker precisa do JSON inteiro p/ parsear
    private static final int BODY_CACHE_LIMIT_BYTES = 16384;

    private final SensitiveDataMasker masker;

    public RequestLoggingFilter(SensitiveDataMasker masker) {
        this.masker = masker;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = resolveTraceId(request);
        MDC.put(TRACE_ID_MDC_KEY, traceId);

        ContentCachingRequestWrapper requestWrapper =
            new ContentCachingRequestWrapper(request, BODY_CACHE_LIMIT_BYTES);
        ContentCachingResponseWrapper responseWrapper =
            new ContentCachingResponseWrapper(response);
        responseWrapper.setHeader(TRACE_ID_HEADER, traceId);

        long start = System.nanoTime();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            try {
                long durationMs = (System.nanoTime() - start) / 1_000_000;
                logRequestBody(requestWrapper);
                LOGGER.info(
                    "{} {} -> {} ({} ms)",
                    requestWrapper.getMethod(),
                    requestPath(requestWrapper),
                    responseWrapper.getStatus(),
                    durationMs
                );
                responseWrapper.copyBodyToResponse();
            } finally {
                MDC.clear();
            }
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String incoming = request.getHeader(TRACE_ID_HEADER);
        if (incoming != null && incoming.matches(TRACE_ID_PATTERN)) {
            return incoming;
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String requestPath(HttpServletRequest request) {
        String query = request.getQueryString();
        return query == null
            ? request.getRequestURI()
            : request.getRequestURI() + "?" + masker.maskQueryString(query);
    }

    private void logRequestBody(ContentCachingRequestWrapper request) {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return;
        }
        String masked = masker.mask(
            new String(content, StandardCharsets.UTF_8)
        );
        if (masked.length() > MAX_BODY_LOG_LENGTH) {
            masked = masked.substring(0, MAX_BODY_LOG_LENGTH) + "...";
        }
        LOGGER.debug("request body: {}", masked);
    }
}
