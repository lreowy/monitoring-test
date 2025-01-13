package com.cakey_practice.cakey.dto;

import com.cakey_practice.cakey.auth.SocialType;

public record LoginReq(
        SocialType socialType
) {
}
