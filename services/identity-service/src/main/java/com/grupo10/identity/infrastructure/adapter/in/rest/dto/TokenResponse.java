package com.grupo10.identity.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse(
        @JsonProperty("access_token")  String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type")    String tokenType,
        @JsonProperty("expires_in")    int expiresIn
) {
    public static TokenResponse login(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", 900);
    }

    public static TokenResponse refresh(String accessToken) {
        return new TokenResponse(accessToken, null, "Bearer", 900);
    }
}
