package com.fiap.restaurant_management_v2.infrastructure.security;

import com.fiap.restaurant_management_v2.application.gateways.GeneratedToken;
import com.fiap.restaurant_management_v2.application.gateways.TokenGateway;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGateway implements TokenGateway {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public JwtTokenGateway(
        JwtEncoder jwtEncoder,
        @Value("${jwt.access-token-expiration-minutes}") long expirationMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public GeneratedToken generate(UUID userId, String login, String authority) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        List<String> roles = authority == null ? List.of() : List.of(authority);
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .claim("login", login)
            .claim("roles", roles)
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new GeneratedToken(token, expiresAt);
    }
}
