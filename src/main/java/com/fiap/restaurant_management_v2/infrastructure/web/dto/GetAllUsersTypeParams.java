package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetAllUsersTypeParams(
    @Schema(example = "1") Integer page,
    @Schema(example = "20") Integer size
) {
    public GetAllUsersTypeParams {
        page = (page == null || page < 1) ? 1 : page;
        size = (size == null || size < 1) ? 10 : Math.min(size, 100);
    }
}
