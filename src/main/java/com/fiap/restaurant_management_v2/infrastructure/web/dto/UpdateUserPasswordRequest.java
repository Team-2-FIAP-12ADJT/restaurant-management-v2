package com.fiap.restaurant_management_v2.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserPasswordRequest(
    @NotBlank(message = "Old password is required")
    @Size(
        min = 6,
        max = 12,
        message = "Old password must be between 6 and 12 characters long"
    )
    @Schema(example = "Senh@123") String oldPassword,
    @NotBlank(message = "New password is required")
    @Size(
        min = 6,
        max = 12,
        message = "New password must be between 6 and 12 characters long"
    )
    @Schema(example = "Senh@456") String newPassword
) {
    /**
     * Compact constructor normalizes without throwing. Validation happens in Bean
     * Validation annotations above, so missing/invalid fields become
     * MethodArgumentNotValidException (per-field), not HttpMessageNotReadableException
     * (opaque).
     */
    public UpdateUserPasswordRequest {
        oldPassword = oldPassword != null ? oldPassword.trim() : null;
        newPassword = newPassword != null ? newPassword.trim() : null;
    }
}
