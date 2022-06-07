package com.jinwon.ssoauth.infra.config.jwt.enums;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

// todo
public enum JwtException {

    SIGNATURE(SignatureException.class, ),
    MALFORMED(MalformedJwtException.class),
    EXPIRED(ExpiredJwtException.class),
    UNSUPPORTED(UnsupportedJwtException.class),
    ILLEGAL(IllegalArgumentException.class),
    DEFAULT(Exception.class)
    ;

    private final Class<?> exceptionClass;

    JwtException(Class<?> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }
}
