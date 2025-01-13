package com.cakey_practice.cakey.service;

import com.cakey_practice.cakey.dto.LoginReq;
import com.cakey_practice.cakey.dto.UserInfoRes;

public interface SocialService {
    UserInfoRes login(final String authorizationToken, final LoginReq loginReq);
}
