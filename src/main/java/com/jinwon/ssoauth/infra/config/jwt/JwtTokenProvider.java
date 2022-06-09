package com.jinwon.ssoauth.infra.config.jwt;

import com.jinwon.ssoauth.domain.entity.user.User;
import com.jinwon.ssoauth.infra.config.jwt.enums.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT 토큰 인증
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${security.oauth2.jwt.sign.key}")
    private String signKey;

    private static final String ISSUER = "SSO-AUTH";

    /* 변경 시 TokenRedisComponent TTL 변경 필요 */
    private static final int TOKEN_EXPIRED = 1;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";

    /**
     * accessToken 생성
     *
     * @param user 사용자 정보
     */
    public String generateToken(@NotNull User user) {
        final Instant now = Instant.now();

        final String id = String.valueOf(user.getId());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(ISSUER)
                .setIssuedAt(Date.from(now))
                .setSubject(id)
                .setId(user.getUid())
                .setExpiration(Date.from(now.plus(TOKEN_EXPIRED, ChronoUnit.HOURS)))
                .claim(ID, id)
                .claim(NAME, user.getName())
                .claim(EMAIL, user.getEmail())
                .signWith(SignatureAlgorithm.HS512, signKey)
                .compact();
    }

    /**
     * refreshToken 생성
     */
    public String generateRefreshToken() {
        final Instant now = Instant.now();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(ISSUER)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(TOKEN_EXPIRED, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS512, signKey)
                .compact();
    }

    /**
     * 유효한 토큰 여부 반환
     *
     * @param accessToken JWT 토큰
     */
    public boolean validateToken(String accessToken) {
        try {
            final Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(signKey)
                    .parseClaimsJws(accessToken);

            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception ex) {
            log.error(JwtException.getMessageByExceptionClass(ex.getClass()));
            log.error(ExceptionUtils.getStackTrace(ex));
            return false;
        }
    }

}
