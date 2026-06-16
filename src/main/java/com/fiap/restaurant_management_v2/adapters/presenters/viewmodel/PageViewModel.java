package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

import java.util.List;

public record PageViewModel<T>(
    int page,
    int size,
    long totalElements,
    int totalPages,
    List<T> content
) {}
