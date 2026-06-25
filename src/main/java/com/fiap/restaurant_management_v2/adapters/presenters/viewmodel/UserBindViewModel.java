package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

import java.util.UUID;

public record UserBindViewModel(
    UUID id,
    UUID typeId
) {}
