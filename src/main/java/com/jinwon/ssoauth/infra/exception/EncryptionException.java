package com.jinwon.ssoauth.infra.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 암복호화 Exception
 */
@Getter
@AllArgsConstructor
public class EncryptionException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "암복호화 오류";

    public String getDefaultMessage() {
        return DEFAULT_MESSAGE;
    }

}
