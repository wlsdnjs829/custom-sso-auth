package com.jinwon.ssoauth.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@ApiModel(value = "예외 핸들러 객체")
public class ErrorDto {

    @ApiModelProperty(value = "상태")
    private final HttpStatus status;

    @ApiModelProperty(value = "메시지 코드")
    private final String messageCode;

    public ErrorDto(HttpStatus status, String messageCode) {
        this.status = status;
        this.messageCode = messageCode;
    }

}
