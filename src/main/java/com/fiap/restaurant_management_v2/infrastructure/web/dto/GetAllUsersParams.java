package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetAllUsersParams(
    @Schema(example = "João") String name,
    @Schema(example = "joao@example.com") String email,
    @Schema(example = "joaosilva") String login,
    @Schema(example = "12345678901") String taxIdentifier,
    @Schema(example = "1") Integer page,
    @Schema(example = "20") Integer size
) {
    public GetAllUsersParams {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
