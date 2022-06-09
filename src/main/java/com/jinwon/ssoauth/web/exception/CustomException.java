package com.jinwon.ssoauth.web.exception;

import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final TokenMessage tokenMessage;

}
