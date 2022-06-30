package com.jinwon.ssoauth.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Schema(description = "로그인 객체")
public class LoginDto {

    @NotNull
    @Schema(description = "아이디", required = true)
    private String email;

    @NotNull
    @Schema(description = "고유 코드", required = true)
    private String password;

}
