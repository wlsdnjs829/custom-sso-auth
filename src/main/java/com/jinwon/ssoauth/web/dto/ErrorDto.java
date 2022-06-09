package com.jinwon.ssoauth.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "예외 핸들러 객체")
public class ErrorDto {

    @ApiModelProperty(value = "발생 시간")
    private final LocalDateTime timestamp;

    @ApiModelProperty(value = "상태")
    private final HttpStatus status;

    @ApiModelProperty(value = "에러 코드")
    private final String error;

    public ErrorDto(HttpStatus status, String error) {
        this.status = status;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

}
