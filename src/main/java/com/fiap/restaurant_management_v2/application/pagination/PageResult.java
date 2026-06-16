package com.fiap.restaurant_management_v2.application.pagination;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    long totalElements,
    int page,
    int size
) {
    public int totalPages() {
        return size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }
}
