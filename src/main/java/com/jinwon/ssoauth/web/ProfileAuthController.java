package com.jinwon.ssoauth.web;

import com.jinwon.ssoauth.domain.entity.profile.Profile;
import com.jinwon.ssoauth.infra.component.TokenRedisComponent;
import com.jinwon.ssoauth.infra.config.jwt.JwtTokenProvider;
import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import com.jinwon.ssoauth.infra.config.security.CustomUserDetailService;
import com.jinwon.ssoauth.infra.utils.NetworkUtil;
import com.jinwon.ssoauth.web.dto.JwtTokenDto;
import com.jinwon.ssoauth.web.dto.LoginDto;
import com.jinwon.ssoauth.web.exception.CustomException;
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
@Tag(name = "프로필 인증 컨트롤러")
public class ProfileAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisComponent tokenRedisComponent;
    private final CustomUserDetailService userDetailService;

    @GetMapping("/profile/me")
    @Operation(summary = "사용자 정보 조회")
    public Mono<Principal> profile(Principal principal) {
        return Mono.just(principal);
    }

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인 토큰 발급")
    public Mono<JwtTokenDto> login(HttpServletRequest request, @Valid @RequestBody LoginDto loginDto) {
        final Profile profile = userDetailService.validProfileThrowIfInvalid(loginDto.getEmail(), loginDto.getPassword());
        final String clientIp = NetworkUtil.getClientIp(request);
        final Profile loginProfile = addProfileInfo(profile, clientIp);

        final JwtTokenDto jwtTokenDto = new JwtTokenDto(loginProfile.getAccessToken(), loginProfile.getRefreshToken());
        return Mono.just(jwtTokenDto);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "사용자 토큰 재사용 요청")
    public Mono<JwtTokenDto> login(HttpServletRequest request, @Valid @RequestBody JwtTokenDto jwtTokenDto) {
        final String expiredAccessToken = jwtTokenDto.getToken();
        final String expiredRefreshToken = jwtTokenDto.getRefreshToken();

        final Optional<Profile> accessProfile = tokenRedisComponent.getTokenProfile(expiredAccessToken);
        final boolean validateToken = jwtTokenProvider.validateToken(expiredAccessToken);

        if (accessProfile.isPresent() && validateToken) {
            throw new CustomException(TokenMessage.NON_EXPIRED);
        }

        tokenRedisComponent.deleteValues(expiredAccessToken);

        final Profile refreshProfile = tokenRedisComponent.getTokenProfile(expiredRefreshToken)
                .orElseThrow(() -> new CustomException(TokenMessage.EXPIRED_REFRESH_TOKEN));

        tokenRedisComponent.deleteValues(expiredRefreshToken);

        final String clientIp = NetworkUtil.getClientIp(request);
        final Profile profile = addProfileInfo(refreshProfile, clientIp);
        final JwtTokenDto jwtToken = new JwtTokenDto(profile.getAccessToken(), profile.getRefreshToken());
        return Mono.just(jwtToken);
    }

    /* 프로필 추가 정보 저장 */
    private Profile addProfileInfo(Profile profile, String clientIp) {
        final String token = jwtTokenProvider.generateToken(profile);
        final String refreshToken = jwtTokenProvider.generateRefreshToken();

        final Profile loginProfile = profile.accessToken(token)
                .refreshToken(refreshToken)
                .clientIp(clientIp);

        tokenRedisComponent.addAccessToken(token, loginProfile);
        tokenRedisComponent.addRefreshToken(refreshToken, loginProfile);
        return profile;
    }

    @PostMapping("/logout")
    @Operation(summary = "프로필 로그아웃")
    public Mono<Boolean> logout(Authentication authentication) {
        @SuppressWarnings("unchecked") final Optional<Profile> profileOp = (Optional<Profile>) authentication.getPrincipal();

        final Profile profile = profileOp.orElseThrow(IllegalArgumentException::new);

        final String accessToken = profile.getAccessToken();
        final String refreshToken = profile.getRefreshToken();

        return Mono.just(
                tokenRedisComponent.deleteValues(accessToken) && tokenRedisComponent.deleteValues(refreshToken));
    }

}
