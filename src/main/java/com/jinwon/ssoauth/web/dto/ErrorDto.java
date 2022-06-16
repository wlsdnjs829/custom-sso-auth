package com.jinwon.ssoauth.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Schema(description = "예외 핸들러 객체")
public class ErrorDto {

    @Schema(description = "발생 시간")
    private final LocalDateTime timestamp;

    @Schema(description = "상태")
    private final HttpStatus status;

    @Schema(description = "에러 코드")
    private final String error;

    public ErrorDto(HttpStatus status, String error) {
        this.status = status;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

}
