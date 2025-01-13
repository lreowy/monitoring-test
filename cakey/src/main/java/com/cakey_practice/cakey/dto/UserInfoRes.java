package com.cakey_practice.cakey.dto;

import com.cakey_practice.cakey.auth.SocialType;

public record UserInfoRes(
        Long socialId,
        String email,
        SocialType socialType
) {
    public static UserInfoRes of(
            final Long socialId,
            final String email,
            final SocialType socialType
    ) {
        return new UserInfoRes(socialId, email, socialType);
    }
}
