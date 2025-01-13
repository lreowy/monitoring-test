package com.cakey_practice.cakey.controller;

import com.cakey_practice.cakey.dto.LoginReq;
import com.cakey_practice.cakey.dto.LoginSuccessRes;
import com.cakey_practice.cakey.service.TokenService;
import com.cakey_practice.cakey.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/v1/user/login")
    public ResponseEntity login(@RequestHeader final String authorization,
                                @RequestBody final LoginReq loginReq,
                                HttpServletResponse response
    ) {
        LoginSuccessRes loginSuccessRes = userService.create(authorization, loginReq);
        response.addHeader("Set-Cookie", userService.accessCookie(loginSuccessRes).toString());
        response.addHeader("Set-Cookie", userService.refreshCookie(loginSuccessRes).toString());
        return ResponseEntity.ok().body(loginSuccessRes);
    }

    @PostMapping("/v1/user/logout")
    public ResponseEntity logout(final Principal principal){
        tokenService.deleteRefreshToken(Long.valueOf(principal.getName()));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //로그인 필수
    @GetMapping("/v1/user/test1")
    public ResponseEntity test1(final Principal principal){
        return ResponseEntity.ok().body(principal.getName());
    }

    //로그인 필수는 아닌데 암튼 그럼
    @GetMapping("/v1/user/test2")
    public ResponseEntity test2(final Principal principal){
        return ResponseEntity.ok().body(principal.getName());
    }
}