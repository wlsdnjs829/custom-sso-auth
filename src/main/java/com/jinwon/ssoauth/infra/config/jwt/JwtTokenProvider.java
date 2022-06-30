package com.jinwon.ssoauth.infra.config.jwt;

import com.jinwon.ssoauth.domain.member.Member;
import com.jinwon.ssoauth.infra.config.jwt.enums.JwtException;
import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import com.jinwon.ssoauth.infra.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT 토큰 인증
 */
@Slf4j
@Component
//@RefreshScope
public class JwtTokenProvider {

    @Value("${spring.security.oauth2.jwt.alias}")
    private String jwtAlias;

    @Value("${spring.security.oauth2.jwt.code}")
    private String jwtCode;

    @Value("${spring.security.oauth2.jwt.expired}")
    private int tokenExpired;

    @Value("${spring.security.oauth2.jwt.public}")
    private String publicPath;

    @Value("${spring.security.oauth2.jwt.private}")
    private String privatePath;

    private KeyStore keyStore;

    private static final String ISSUER = "SSO-AUTH";

    private static final String USER_ID = "userId";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String RSA = "RSA";
    private static final String JKS = "JKS";

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance(JKS);
            final InputStream resourceAsStream = getClass().getResourceAsStream(privatePath);
            keyStore.load(resourceAsStream, jwtCode.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new CustomException(TokenMessage.EXPIRED_REFRESH_TOKEN);
        }

    }

    /**
     * accessToken 생성
     *
     * @param member 사용자 정보
     */
    public String generateToken(@NotNull Member member) {
        final Instant now = Instant.now();

        final String id = String.valueOf(member.getMemberId());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(ISSUER)
                .setIssuedAt(Date.from(now))
                .setSubject(id)
                .setId(member.getEmail())
                .setExpiration(Date.from(now.plus(tokenExpired, ChronoUnit.HOURS)))
                .claim(USER_ID, id)
                .claim(NAME, member.getName())
                .claim(EMAIL, member.getEmail())
                .signWith(SignatureAlgorithm.RS512, getPrivateKey())
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
                .setExpiration(Date.from(now.plus(tokenExpired, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.RS512, getPrivateKey())
                .compact();
    }

    /* 개인 키 발급 */
    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(jwtAlias, jwtCode.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new CustomException(TokenMessage.EXPIRED_REFRESH_TOKEN);
        }
    }

    /**
     * 유효한 토큰 여부 반환
     *
     * @param accessToken JWT 토큰
     */
    public boolean validateToken(String accessToken) {
        try {
            final Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(getPublicKey())
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

    /* 공개 키 발급 */
    private PublicKey getPublicKey() {
        try {
            final Resource resource = new ClassPathResource(publicPath);
            final String publicKey = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8.name());
            final byte[] publicBytes = Base64.decodeBase64(publicKey);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            final KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CustomException(TokenMessage.EXPIRED_REFRESH_TOKEN);
        }
    }

}
