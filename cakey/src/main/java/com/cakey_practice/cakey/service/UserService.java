package com.cakey_practice.cakey.service;

import com.cakey_practice.cakey.auth.JwtTokenProvider;
import com.cakey_practice.cakey.auth.SocialType;
import com.cakey_practice.cakey.auth.UserAuthentication;
import com.cakey_practice.cakey.domain.User;
import com.cakey_practice.cakey.dto.AccessTokenGetSuccess;
import com.cakey_practice.cakey.dto.LoginReq;
import com.cakey_practice.cakey.dto.LoginSuccessRes;
import com.cakey_practice.cakey.dto.UserInfoRes;
import com.cakey_practice.cakey.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final KakaoSocialService kakaoSocialService;

    private final static String ACCESS_TOKEN = "accessToken";
    private final static String REFRESH_TOKEN = "refreshToken";
    public LoginSuccessRes create(
            final String authorizationCode,
            final LoginReq loginReq
            ) {
        return getTokenDto(kakaoSocialService.login(authorizationCode, loginReq));
    }

    public ResponseCookie accessCookie(LoginSuccessRes loginSuccessRes) {
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN, loginSuccessRes.accessToken())
                .maxAge(60 * 60 * 7 * 24)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        return accessCookie;
    }

    public ResponseCookie refreshCookie(LoginSuccessRes loginSuccessRes) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN,loginSuccessRes.refreshToken())
                .maxAge(60 * 60 * 7 * 24)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        return refreshCookie;
    }

    public UserInfoRes getUserInfo(final String authorizationCode,
                                   final LoginReq loginReq
    ) {
        switch (loginReq.socialType()){
            case KAKAO:
                return kakaoSocialService.login(authorizationCode, loginReq);
            default:
                throw new RuntimeException("Social type not supported");
        }
    }

    public Long createUser(final UserInfoRes userInfoRes) {
        User user = User.of(
                userInfoRes.socialId(),
                userInfoRes.email(),
                userInfoRes.socialType()
        );
        return userRepository.save(user).getId();
    }

    public User getBySocialId(
            final Long socialId,
            final SocialType socialType
            )
    {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        return user;
    }

    public AccessTokenGetSuccess refreshToken(final String refreshToken) {
        Long userId = jwtTokenProvider.getUserFromJwt(refreshToken);
        if(!userId.equals(tokenService.findIdByRefreshToken(refreshToken))){
            throw new RuntimeException("Invalid refresh token");
        }

        UserAuthentication userAuthentication = new UserAuthentication(userId, null, null);
        return AccessTokenGetSuccess.of(
                jwtTokenProvider.issueAccessToken(userAuthentication)
        );
    }

    public boolean isExistingUser(
            final Long socialId,
            final SocialType socialType
    ) {
        return userRepository.findBySocialTypeAndSocialId(socialType, socialId).isPresent();
    }

    public LoginSuccessRes getTokenByUserId(
            final Long id
    ) {
        UserAuthentication userAuthentication = new UserAuthentication(id, null, null);
        String refreshToken = jwtTokenProvider.issueRefreshToken(userAuthentication);
        tokenService.saveRefreshToken(id, refreshToken);
        return LoginSuccessRes.of(
                jwtTokenProvider.issueAccessToken(userAuthentication),
                refreshToken
        );
    }
    private LoginSuccessRes getTokenDto(final UserInfoRes userInfoRes) {
        if(isExistingUser(userInfoRes.socialId(), userInfoRes.socialType())){
            return getTokenByUserId(getBySocialId(userInfoRes.socialId(), userInfoRes.socialType()).getId());
        }
        else {
            Long id = createUser(userInfoRes);
            return getTokenByUserId(id);
        }
    }

}
