package com.jinwon.ssoauth.web;

import com.jinwon.ssoauth.domain.entity.user.User;
import com.jinwon.ssoauth.infra.component.TokenRedisComponent;
import com.jinwon.ssoauth.infra.config.jwt.JwtTokenProvider;
import com.jinwon.ssoauth.infra.config.security.CustomUserDetailService;
import com.jinwon.ssoauth.infra.utils.NetworkUtil;
import com.jinwon.ssoauth.web.dto.JwtTokenDto;
import com.jinwon.ssoauth.web.dto.LoginDto;
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

import static com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage.EXPIRED_REFRESH_TOKEN;
import static com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage.NON_EXPIRED;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisComponent tokenRedisComponent;
    private final CustomUserDetailService userDetailService;

    @GetMapping("/user/me")
    public Principal user(Principal principal) {
        return principal;
    }

    @PostMapping("/login")
    public JwtTokenDto login(HttpServletRequest request, @Valid @RequestBody LoginDto loginDto) {
        final User user = userDetailService.validUserThrowIfInvalid(loginDto.getUserId(), loginDto.getUserPw());
        final String clientIp = NetworkUtil.getClientIp(request);
        final User loginUser = addInfo(user, clientIp);

        return new JwtTokenDto(loginUser.getAccessToken(), loginUser.getRefreshToken());
    }

    @PostMapping("/refresh-token")
    public JwtTokenDto login(HttpServletRequest request, @Valid @RequestBody JwtTokenDto jwtTokenDto) {
        final String expiredAccessToken = jwtTokenDto.getToken();
        final String expiredRefreshToken = jwtTokenDto.getRefreshToken();

        final Optional<User> accessUser = tokenRedisComponent.getTokenUser(expiredAccessToken);
        final boolean validateToken = jwtTokenProvider.validateToken(expiredAccessToken);

        Assert.isTrue(!validateToken || accessUser.isEmpty(), NON_EXPIRED.getMessage());
        tokenRedisComponent.deleteValues(expiredAccessToken);

        final User refreshUser = tokenRedisComponent.getTokenUser(expiredRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException(EXPIRED_REFRESH_TOKEN.getMessage()));
        tokenRedisComponent.deleteValues(expiredRefreshToken);

        final String clientIp = NetworkUtil.getClientIp(request);
        final User user = addInfo(refreshUser, clientIp);
        return new JwtTokenDto(user.getAccessToken(), user.getRefreshToken());
    }

    private User addInfo(User user, String clientIp) {
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
    public boolean logout(Authentication authentication) {
        @SuppressWarnings("unchecked") final Optional<User> userOp = (Optional<User>) authentication.getPrincipal();

        final User user = userOp.orElseThrow(IllegalArgumentException::new);

        final String accessToken = user.getAccessToken();
        final String refreshToken = user.getRefreshToken();

        return tokenRedisComponent.deleteValues(accessToken) && tokenRedisComponent.deleteValues(refreshToken);
    }

}
