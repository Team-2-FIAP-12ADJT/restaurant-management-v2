package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

/** DS-model returned by the data source. Never carries the password. */
public record UserDsResponseModel(
    UUID id,
    String name,
    String email,
    String login,
    String taxIdentifier,
    UUID userTypeId,
    String userTypeName
) {}
