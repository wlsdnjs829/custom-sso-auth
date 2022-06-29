package com.jinwon.ssoauth.infra.config.jwt.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

/**
 * 클라이언트 용 Token Message Enum
 */
@Getter
public enum TokenMessage {

    SIGNATURE(HttpStatus.FORBIDDEN, "A001", "유효하지 않은 시그니쳐"),
    MALFORMED(HttpStatus.FORBIDDEN, "A002", "유효하지 않은 JWT"),
    EXPIRED(HttpStatus.FORBIDDEN, "A003", "만료된 JWT"),
    UNSUPPORTED(HttpStatus.FORBIDDEN, "A004", "지원하지 않는 JWT"),
    ILLEGAL(HttpStatus.BAD_REQUEST, "A005", "존재하지 않는 JWT 내부 정보"),
    NON_EXPIRED(HttpStatus.BAD_REQUEST, "A006", "만료되지 않은 JWT"),
    MALFORMED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "A007", "유효하지 않은 JWT"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "A008", "만료된 REFRASH TOKEN"),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "A009", "유효하지 않은 사용자 정보"),
    NON_MATCH_USER_CODE(HttpStatus.BAD_REQUEST, "A010", "유효하지 않은 비밀번호"),
    PARSER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "A011", "파싱 실패"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TokenMessage(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public static TokenMessage getErrorMessageByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        return Arrays.stream(TokenMessage.values())
                .filter(errorMessage -> errorMessage.name().equals(name))
                .findFirst()
                .orElse(null);
    }

}
