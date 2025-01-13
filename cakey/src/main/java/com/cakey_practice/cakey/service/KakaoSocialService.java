package com.cakey_practice.cakey.service;

import com.cakey_practice.cakey.auth.KakaoApiClient;
import com.cakey_practice.cakey.auth.KakaoAuthApiClient;
import com.cakey_practice.cakey.auth.SocialType;
import com.cakey_practice.cakey.dto.KakaoAccessTokenRes;
import com.cakey_practice.cakey.dto.KakaoUserResponse;
import com.cakey_practice.cakey.dto.LoginReq;
import com.cakey_practice.cakey.dto.UserInfoRes;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoSocialService implements SocialService {
    private static final String AUTH_CODE = "authorization_code";
    private static final String REDIRECT_URI = "http://localhost:5173/kakao";

    @Value("${kakao.clientId}")
    private String clientId;

    private final KakaoApiClient kakaoApiClient;
    private final KakaoAuthApiClient kakaoAuthApiClient;

    @Transactional
    @Override
    public UserInfoRes login(
            final String authorizationCode,
            final LoginReq loginReq
    ) {
        String accessToken;
        try {
            // 인가 코드로 Access Token + Refresh Token 받아오기
            accessToken = getOAuth2Authentication(authorizationCode);
        } catch (FeignException e) {
            e.printStackTrace();
            throw new RuntimeException("authentication code expired");
        }
        String contentType = MediaType.APPLICATION_FORM_URLENCODED.toString();
        // Access Token으로 유저 정보 불러오기
        return getLoginDto(loginReq.socialType(), getUserInfo(accessToken, contentType));
    }

    private String getOAuth2Authentication(
            final String authorizationCode
    ) {
        KakaoAccessTokenRes response = kakaoAuthApiClient.getOAuth2AccessToken(
                AUTH_CODE,
                clientId,
                REDIRECT_URI,
                authorizationCode
        );
        return response.accessToken();
    }

    private KakaoUserResponse getUserInfo(
            final String accessToken,
            final String contentType
    ) {
        System.out.println("accessToken:" + accessToken);
        return kakaoApiClient.getUserInformation("Bearer " + accessToken, contentType);
    }

    private UserInfoRes getLoginDto(
            final SocialType socialType,
            final KakaoUserResponse userResponse
    ) {
        return UserInfoRes.of(
                userResponse.id(),
                userResponse.kakaoAccount().profile().nickname(),
                socialType
        );
    }
}
