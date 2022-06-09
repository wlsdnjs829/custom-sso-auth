package com.jinwon.ssoauth.web.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * JWT 토큰 송수신 객체
 */
@Getter
public class JwtTokenDto {

    @NotNull
    private final String token;

    @NotNull
    private final String refreshToken;

    public JwtTokenDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

}
