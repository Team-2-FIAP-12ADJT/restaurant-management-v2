package com.fiap.restaurant_management_v2.application.gateways;

import java.util.UUID;

/** DS-model carrying user data into the data source. Password is already hashed. */
public record UserTypeUpdateDsRequestModel(
    UUID id,
    String userType
) {}
