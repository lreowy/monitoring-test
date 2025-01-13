package com.cakey_practice.cakey.auth;

import com.cakey_practice.cakey.dto.KakaoUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    @GetMapping(value = "/v2/user/me", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoUserResponse getUserInformation(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType
    );
}
