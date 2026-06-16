package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

public record GetAllUsersRequestModel(
    String name,
    String email,
    String login,
    int page,
    int size
) {}
