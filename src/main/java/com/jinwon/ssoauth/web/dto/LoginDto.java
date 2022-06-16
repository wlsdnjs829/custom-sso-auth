package com.jinwon.ssoauth.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Schema(description = "로그인 객체")
public class LoginDto {

    @NotNull
    @Schema(description = "사용자 아이디", required = true)
    private String userId;

    @NotNull
    @Schema(description = "사용자 고유 코드", required = true)
    private String userPw;

}
