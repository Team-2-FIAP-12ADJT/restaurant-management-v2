package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

import java.util.UUID;

public record UserSummary(UUID id, String name, String email, String login) {}
