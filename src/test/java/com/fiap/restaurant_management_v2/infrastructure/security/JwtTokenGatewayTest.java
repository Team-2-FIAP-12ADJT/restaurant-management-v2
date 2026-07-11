package com.fiap.restaurant_management_v2.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class JwtTokenGatewayTest {

    private JwtTokenGateway tokenGateway;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        String secretBase64 = "ZGV2LW9ubHktc2VjcmV0LTMyLWJ5dGVzLW1pbi1wYWRkaW5nMDE=";
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();

        tokenGateway = new JwtTokenGateway(encoder, 15);
    }

    @Test
    void generateWithAuthorityCreatesValidToken() {
        UUID userId = UUID.randomUUID();
        var token = tokenGateway.generate(userId, "owner", "DONO");

        assertNotNull(token.token());
        assertNotNull(token.expiresAt());

        var decoded = jwtDecoder.decode(token.token());
        assertEquals(userId.toString(), decoded.getSubject());
        assertEquals("owner", decoded.getClaimAsString("login"));
        assertTrue(decoded.getClaimAsStringList("roles").contains("DONO"));
    }

    @Test
    void generateWithNullAuthorityCreatesEmptyRolesList() {
        UUID userId = UUID.randomUUID();
        var token = tokenGateway.generate(userId, "user", null);

        assertNotNull(token.token());

        var decoded = jwtDecoder.decode(token.token());
        assertEquals(userId.toString(), decoded.getSubject());
        assertEquals("user", decoded.getClaimAsString("login"));
        assertEquals(List.of(), decoded.getClaimAsStringList("roles"));
    }
}
