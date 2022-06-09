package com.jinwon.ssoauth.infra.config.jwt.enums;

import lombok.Getter;

/**
 * 클라이언트 용 Token Message Enum
 */
@Getter
public enum TokenMessage {

    SIGNATURE("유효하지 않은 시그니쳐"),
    MALFORMED("유효하지 않은 JWT"),
    EXPIRED("만료된 JWT"),
    UNSUPPORTED("지원하지 않는 JWT"),
    ILLEGAL("존재하지 않는 JWT 내부 정보"),
    NON_EXPIRED("만료되지 않은 JWT"),
    MALFORMED_REFRESH_TOKEN("유효하지 않은 JWT"),
    EXPIRED_REFRESH_TOKEN("만료된 REFRASH TOKEN");

    private final String message;

    TokenMessage(String message) {
        this.message = message;
    }

}
