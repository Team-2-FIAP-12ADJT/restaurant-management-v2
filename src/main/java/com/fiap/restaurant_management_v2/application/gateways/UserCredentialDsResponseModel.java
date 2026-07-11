package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

public record UserCredentialDsResponseModel(UUID id, String login, String passwordHash, String userTypeName) {}
