package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

import java.time.Instant;

public record TokenViewModel(String accessToken, Instant expiresAt) {}
