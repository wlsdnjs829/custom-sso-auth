package com.jinwon.ssoauth.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Schema(description = "JWT 토큰 송수신 객체")
public class JwtTokenDto {

    @NotNull
    @Schema(description = "인증 토큰", required = true)
    private final String token;

    @NotNull
    @Schema(description = "재사용 토큰", required = true)
    private final String refreshToken;

    public JwtTokenDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

}
