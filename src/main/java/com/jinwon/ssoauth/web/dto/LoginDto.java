package com.jinwon.ssoauth.web.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * 로그인 객체
 */
@Getter
public class LoginDto {

    @NotNull
    private String userId;

    @NotNull
    private String userPw;

}
