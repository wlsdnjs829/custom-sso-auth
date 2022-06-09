package com.jinwon.ssoauth.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@ApiModel(value = "로그인 객체")
public class LoginDto {

    @NotNull
    @ApiModelProperty(value = "사용자 아이디", required = true)
    private String userId;

    @NotNull
    @ApiModelProperty(value = "사용자 고유 코드", required = true)
    private String userPw;

}
