package com.jinwon.ssoauth.web;

import com.jinwon.ssoauth.domain.entity.user.User;
import com.jinwon.ssoauth.infra.component.TokenRedisComponent;
import com.jinwon.ssoauth.infra.config.jwt.JwtTokenProvider;
import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import com.jinwon.ssoauth.infra.config.security.CustomUserDetailService;
import com.jinwon.ssoauth.infra.utils.NetworkUtil;
import com.jinwon.ssoauth.web.dto.JwtTokenDto;
import com.jinwon.ssoauth.web.dto.LoginDto;
import com.jinwon.ssoauth.web.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = "사용자 인증 컨트롤러")
public class UserAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisComponent tokenRedisComponent;
    private final CustomUserDetailService userDetailService;

    @GetMapping("/user/me")
    @ApiOperation(value = "사용자 정보 조회")
    public Principal user(Principal principal) {
        return principal;
    }

    @PostMapping("/login")
    @ApiOperation(value = "사용자 로그인 토큰 발급")
    public JwtTokenDto login(HttpServletRequest request, @Valid @RequestBody LoginDto loginDto) {
        final User user = userDetailService.validUserThrowIfInvalid(loginDto.getUserId(), loginDto.getUserPw());
        final String clientIp = NetworkUtil.getClientIp(request);
        final User loginUser = addUserInfo(user, clientIp);

        return new JwtTokenDto(loginUser.getAccessToken(), loginUser.getRefreshToken());
    }

    @PostMapping("/refresh-token")
    @ApiOperation(value = "사용자 토큰 재사용 요청")
    public JwtTokenDto login(HttpServletRequest request, @Valid @RequestBody JwtTokenDto jwtTokenDto) {
        final String expiredAccessToken = jwtTokenDto.getToken();
        final String expiredRefreshToken = jwtTokenDto.getRefreshToken();

        final Optional<User> accessUser = tokenRedisComponent.getTokenUser(expiredAccessToken);
        final boolean validateToken = jwtTokenProvider.validateToken(expiredAccessToken);

        if (accessUser.isPresent() && validateToken) {
            throw new CustomException(TokenMessage.NON_EXPIRED);
        }

        tokenRedisComponent.deleteValues(expiredAccessToken);

        final User refreshUser = tokenRedisComponent.getTokenUser(expiredRefreshToken)
                .orElseThrow(() -> new CustomException(TokenMessage.EXPIRED_REFRESH_TOKEN));

        tokenRedisComponent.deleteValues(expiredRefreshToken);

        final String clientIp = NetworkUtil.getClientIp(request);
        final User user = addUserInfo(refreshUser, clientIp);
        return new JwtTokenDto(user.getAccessToken(), user.getRefreshToken());
    }

    /* 사용자 추가 정보 저장 */
    private User addUserInfo(User user, String clientIp) {
        final String token = jwtTokenProvider.generateToken(user);
        final String refreshToken = jwtTokenProvider.generateRefreshToken();

        final User loginUser = user.accessToken(token)
                .refreshToken(refreshToken)
                .clientIp(clientIp);

        tokenRedisComponent.addAccessToken(token, loginUser);
        tokenRedisComponent.addRefreshToken(refreshToken, loginUser);
        return user;
    }

    @PostMapping("/logout")
    @ApiOperation(value = "사용자 로그아웃")
    public boolean logout(Authentication authentication) {
        @SuppressWarnings("unchecked") final Optional<User> userOp = (Optional<User>) authentication.getPrincipal();

        final User user = userOp.orElseThrow(IllegalArgumentException::new);

        final String accessToken = user.getAccessToken();
        final String refreshToken = user.getRefreshToken();

        return tokenRedisComponent.deleteValues(accessToken) && tokenRedisComponent.deleteValues(refreshToken);
    }

}
