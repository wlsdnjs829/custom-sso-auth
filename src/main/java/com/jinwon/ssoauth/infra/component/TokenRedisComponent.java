package com.jinwon.ssoauth.infra.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinwon.ssoauth.domain.entity.profile.Profile;
import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import com.jinwon.ssoauth.web.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * 토큰 레디스 기능 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRedisComponent {

    @Value("${spring.security.oauth2.jwt.expired}")
    private int tokenExpired;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String JSON_PARSING_ERROR = "Json Parsing Error";

    /**
     * 레디스 토큰 정보 저장
     *
     * @param token   accessToken
     * @param profile 사용자 정보
     */
    public void addAccessToken(String token, Profile profile) {
        if (Objects.isNull(token) || Objects.isNull(profile)) {
            return;
        }

        final String json = parserJacksonString(profile);
        final ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(token, json, Duration.ofHours(tokenExpired));
    }

    /* 사용자 정보 Json 데이터 변환 */
    private String parserJacksonString(Profile profile) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new Hibernate5Module());
            return objectMapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CustomException(TokenMessage.PARSER_FAILED);
        }
    }

    /**
     * 레디스 재설정 토큰 저장
     *
     * @param refreshToken 재설정 토큰
     * @param profile      사용자 정보
     */
    public void addRefreshToken(String refreshToken, Profile profile) {
        if (Objects.isNull(refreshToken) || Objects.isNull(profile)) {
            return;
        }

        final String json = parserJacksonString(profile);
        final ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(refreshToken, json, Duration.ofDays(tokenExpired));
    }

    /**
     * 토큰 프로필 조회
     *
     * @param token accessToken & refreshToken
     */
    public Optional<Profile> getTokenProfile(String token) {
        final ValueOperations<String, String> values = redisTemplate.opsForValue();
        final String content = values.get(token);

        if (StringUtils.isEmpty(content)) {
            return Optional.empty();
        }

        return getProfile(content);
    }

    /* json 데이터 사용자 정보 변환 */
    private Optional<Profile> getProfile(String content) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();

            return Optional.of(
                    objectMapper.readValue(content, Profile.class));
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            log.error(JSON_PARSING_ERROR);
            return Optional.empty();
        }
    }

    /**
     * 레디스 토큰 정보 삭제 여부 반환
     *
     * @param token accessToken & refreshToken
     */
    public boolean deleteValues(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        return Boolean.TRUE.equals(redisTemplate.delete(token));
    }

}
