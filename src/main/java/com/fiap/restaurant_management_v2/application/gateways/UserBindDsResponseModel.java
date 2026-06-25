package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

public record UserBindDsResponseModel(
    UUID id,
    String name,
    String email,
    String login,
    String passWord
) {}
