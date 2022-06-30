package com.jinwon.ssoauth.domain.member;

import com.jinwon.ssoauth.domain.member.Member;
import com.jinwon.ssoauth.domain.member.dto.LoginDto;
import com.jinwon.ssoauth.infra.exception.CustomException;
import com.jinwon.ssoauth.infra.component.TokenRedisComponent;
import com.jinwon.ssoauth.infra.config.jwt.JwtTokenProvider;
import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import com.jinwon.ssoauth.infra.config.security.CustomUserDetailService;
import com.jinwon.ssoauth.infra.utils.NetworkUtil;
import com.jinwon.ssoauth.model.JwtTokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 인증 컨트롤러")
public class MemberAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisComponent tokenRedisComponent;
    private final CustomUserDetailService userDetailService;

    @GetMapping("/member/me")
    @Operation(summary = "사용자 정보 조회")
    public Mono<Principal> member(Principal principal) {
        return Mono.just(principal);
    }

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인 토큰 발급")
    public Mono<JwtTokenDto> login(HttpServletRequest request, @Valid @RequestBody LoginDto loginDto) {
        final Member member = userDetailService.validMemberThrowIfInvalid(loginDto.getEmail(), loginDto.getPassword());
        final String clientIp = NetworkUtil.getClientIp(request);
        final Member loginMember = addMemberInfo(member, clientIp);

        final JwtTokenDto jwtTokenDto = new JwtTokenDto(loginMember.getAccessToken(), loginMember.getRefreshToken());
        return Mono.just(jwtTokenDto);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "사용자 토큰 재사용 요청")
    public Mono<JwtTokenDto> login(HttpServletRequest request, @Valid @RequestBody JwtTokenDto jwtTokenDto) {
        final String expiredAccessToken = jwtTokenDto.getToken();
        final String expiredRefreshToken = jwtTokenDto.getRefreshToken();

        final Optional<Member> accessMember = tokenRedisComponent.getTokenMember(expiredAccessToken);
        final boolean validateToken = jwtTokenProvider.validateToken(expiredAccessToken);

        if (accessMember.isPresent() && validateToken) {
            throw new CustomException(TokenMessage.NON_EXPIRED);
        }

        tokenRedisComponent.deleteValues(expiredAccessToken);

        final Member refreshMember = tokenRedisComponent.getTokenMember(expiredRefreshToken)
                .orElseThrow(() -> new CustomException(TokenMessage.EXPIRED_REFRESH_TOKEN));

        tokenRedisComponent.deleteValues(expiredRefreshToken);

        final String clientIp = NetworkUtil.getClientIp(request);
        final Member member = addMemberInfo(refreshMember, clientIp);
        final JwtTokenDto jwtToken = new JwtTokenDto(member.getAccessToken(), member.getRefreshToken());
        return Mono.just(jwtToken);
    }

    /* 회원 추가 정보 저장 */
    private Member addMemberInfo(Member member, String clientIp) {
        final String token = jwtTokenProvider.generateToken(member);
        final String refreshToken = jwtTokenProvider.generateRefreshToken();

        final Member loginMember = member.accessToken(token)
                .refreshToken(refreshToken)
                .clientIp(clientIp);

        tokenRedisComponent.addAccessToken(token, loginMember);
        tokenRedisComponent.addRefreshToken(refreshToken, loginMember);
        return member;
    }

    @PostMapping("/logout")
    @Operation(summary = "사용자 로그아웃")
    public Mono<Boolean> logout(Authentication authentication) {
        @SuppressWarnings("unchecked")
        final Optional<Member> memberOp = (Optional<Member>) authentication.getPrincipal();

        final Member member = memberOp.orElseThrow(IllegalArgumentException::new);

        final String accessToken = member.getAccessToken();
        final String refreshToken = member.getRefreshToken();

        return Mono.just(
                tokenRedisComponent.deleteValues(accessToken) && tokenRedisComponent.deleteValues(refreshToken));
    }

}
