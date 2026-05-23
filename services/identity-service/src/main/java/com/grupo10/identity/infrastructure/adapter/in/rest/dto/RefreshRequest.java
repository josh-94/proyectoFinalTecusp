package com.grupo10.identity.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank @JsonProperty("refresh_token") String refreshToken
) {}
