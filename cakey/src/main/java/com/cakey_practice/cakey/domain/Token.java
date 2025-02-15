package com.cakey_practice.cakey.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToekn", timeToLive = 60 * 60 * 24 * 1000L * 14)
@AllArgsConstructor
@Getter
@Builder
public class Token {

    @Id
    private Long id;

    private String accessToken;

    private String refreshToken;

    public static Token of(
            final Long id,
            final String refreshToken
    ) {
        return Token.builder()
                .id(id)
                .refreshToken(refreshToken)
                .build();
    }

    public static Token of(
            final Long id,
            final String accessToken,
            final String refreshToken
    ) {
        return Token.builder()
                .id(id)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
