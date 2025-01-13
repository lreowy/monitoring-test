package com.cakey_practice.cakey.service;

import com.cakey_practice.cakey.domain.Token;
import com.cakey_practice.cakey.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void saveRefreshToken(final Long userId, final String refreshToken) {
        tokenRepository.save(Token.of(userId, refreshToken));
    }

    public Long findIdByRefreshToken(
            final String refreshToken
    ) {
        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(
                        () -> new RuntimeException("Refresh token not found")
                );
        return token.getId();
    }

    @Transactional
    public void deleteRefreshToken(
            final Long userId
    ) {
        Token token = tokenRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Refresh token not found")
        );
        tokenRepository.delete(token);
    }
}
