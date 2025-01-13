package com.cakey_practice.cakey.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoAccessTokenRes(
        String accessToken
) {
    public static KakaoAccessTokenRes of(final String accessToken) {
        return new KakaoAccessTokenRes(accessToken);
    }
}
