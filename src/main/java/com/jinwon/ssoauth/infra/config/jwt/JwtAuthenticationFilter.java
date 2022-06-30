package com.jinwon.ssoauth.infra.config.jwt;

import com.jinwon.ssoauth.domain.member.Member;
import com.jinwon.ssoauth.infra.component.TokenRedisComponent;
import com.jinwon.ssoauth.infra.utils.NetworkUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * JWT 인증 필터
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final TokenRedisComponent tokenRedisComponent;

    private static final String JWT_PREFIX = "Bearer ";
    private static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, TokenRedisComponent tokenRedisComponent) {
        this.tokenProvider = tokenProvider;
        this.tokenRedisComponent = tokenRedisComponent;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String jwt = getJwtFromRequest(request);
        final String clientIp = NetworkUtil.getClientIp(request);

        getAuthenticationOp(jwt, clientIp)
                .ifPresent(authentication ->
                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication));

        filterChain.doFilter(request, response);
    }

    /**
     * Authentication Optional 조회, 유효하지 않은 경우 Empty 반환
     *
     * @param jwt accessToken
     * @param clientIp 사용자 IP
     */
    private Optional<UsernamePasswordAuthenticationToken> getAuthenticationOp(String jwt, String clientIp) {
        if (!StringUtils.hasText(jwt) || !tokenProvider.validateToken(jwt)) {
            return Optional.empty();
        }

        final Optional<Member> userOp = tokenRedisComponent.getTokenMember(jwt);

        if (userOp.isEmpty()) {
            return Optional.empty();
        }

        final String userClientIp = userOp.map(Member::getClientIp)
                .orElse(org.apache.commons.lang3.StringUtils.EMPTY);

        if (!org.apache.commons.lang3.StringUtils.equals(userClientIp, clientIp)) {
            return Optional.empty();
        }

        final Collection<? extends GrantedAuthority> authorities =
                userOp.map(Member::getAuthorities)
                        .orElseGet(Collections::emptyList);

        return Optional.of(new UsernamePasswordAuthenticationToken(userOp, null, authorities));
    }

    /* JWT 토큰 조회 */
    private String getJwtFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader(X_AUTH_TOKEN);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_PREFIX)) {
            return bearerToken.replace(JWT_PREFIX, org.apache.commons.lang3.StringUtils.EMPTY);
        }

        return null;
    }

}
