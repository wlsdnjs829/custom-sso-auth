package com.jinwon.ssoauth.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "JWT 토큰 송수신 객체")
public class JwtTokenDto {

    @NotNull
    @ApiModelProperty(value = "인증 토큰", required = true)
    private final String token;

    @NotNull
    @ApiModelProperty(value = "재사용 토큰", required = true)
    private final String refreshToken;

    public JwtTokenDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

}
