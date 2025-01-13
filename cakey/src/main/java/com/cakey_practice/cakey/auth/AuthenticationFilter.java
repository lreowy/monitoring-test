package com.cakey_practice.cakey.auth;

import com.cakey_practice.cakey.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String token = getAccessTokenFromCookie(request);
            if (token == null) {
                throw new RuntimeException("Access token is empty");
            }

            if (jwtTokenProvider.validateToken(token) == JwtValidationType.INVALID_JWT_TOKEN) {
                throw new RuntimeException("Access token is invalid");
            }

            final Long userId = jwtTokenProvider.getUserFromJwt(token);
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userId, null, null));

        } catch (Exception e) {
        }
        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromCookie(final HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getJwtFromRequest(final HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
}
