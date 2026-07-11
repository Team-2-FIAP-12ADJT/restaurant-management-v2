package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

public interface TokenGateway {
    GeneratedToken generate(UUID userId, String login, String authority);
}
