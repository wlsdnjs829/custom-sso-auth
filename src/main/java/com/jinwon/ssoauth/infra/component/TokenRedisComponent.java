package com.jinwon.ssoauth.infra.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinwon.ssoauth.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

    private final RedisTemplate<String, String> redisTemplate;

    /* 변경 시 JwtTokenProvider 같이 변경 필요 */
    private static final int HOURS = 1;

    private static final String JSON_PARSING_ERROR = "Json Parsing Error";

    /**
     * 레디스 토큰 정보 저장
     *
     * @param token accessToken
     * @param user  사용자 정보
     */
    public void addAccessToken(String token, User user) {
        if (Objects.isNull(token) || Objects.isNull(user)) {
            return;
        }

        final String json = parserJacksonString(user);
        final ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(token, json, Duration.ofHours(HOURS));
    }

    /* 사용자 정보 Json 데이터 변환 */
    private String parserJacksonString(User user) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            log.error(JSON_PARSING_ERROR);
            throw new IllegalArgumentException();
        }
    }

    /**
     * 레디스 재설정 토큰 저장
     *
     * @param refreshToken 재설정 토큰
     * @param user         사용자 정보
     */
    public void addRefreshToken(String refreshToken, User user) {
        if (Objects.isNull(refreshToken) || Objects.isNull(user)) {
            return;
        }

        final String json = parserJacksonString(user);
        final ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(refreshToken, json, Duration.ofDays(HOURS));
    }

    /**
     * 토큰 사용자 조회
     *
     * @param token accessToken & refreshToken
     */
    public Optional<User> getTokenUser(String token) {
        final ValueOperations<String, String> values = redisTemplate.opsForValue();
        final String content = values.get(token);

        if (StringUtils.isEmpty(content)) {
            return Optional.empty();
        }

        return getUser(content);
    }

    /* json 데이터 사용자 정보 변환 */
    private Optional<User> getUser(String content) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();

            return Optional.of(
                    objectMapper.readValue(content, User.class));
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
