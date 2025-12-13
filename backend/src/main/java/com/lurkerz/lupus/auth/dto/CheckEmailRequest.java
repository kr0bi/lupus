package com.lurkerz.lupus.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckEmailRequest(
        @NotBlank String email
) {
}
