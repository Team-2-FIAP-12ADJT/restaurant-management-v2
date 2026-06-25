package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

public record UserTypeDsResponseModel(
    UUID id,
    String userType
) {}
