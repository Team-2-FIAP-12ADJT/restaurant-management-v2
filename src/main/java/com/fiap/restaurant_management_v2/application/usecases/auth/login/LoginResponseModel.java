package com.fiap.restaurant_management_v2.application.usecases.auth.login;

import java.time.Instant;

public record LoginResponseModel(String accessToken, Instant expiresAt) {}
