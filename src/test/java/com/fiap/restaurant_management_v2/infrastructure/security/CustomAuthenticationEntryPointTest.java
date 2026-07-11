package com.fiap.restaurant_management_v2.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;
    private HttpServletResponse response;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws IOException {
        entryPoint = new CustomAuthenticationEntryPoint();
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @Test
    void commenceWritesUnauthorizedProblemDetail() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/problem+json");
        verify(response).setCharacterEncoding("UTF-8");
        String body = stringWriter.toString();
        assertTrue(body.contains("\"status\":401"));
        assertTrue(body.contains("\"title\":\"Unauthorized\""));
        assertTrue(body.contains("\"detail\":\"Autenticação necessária\""));
    }
}
