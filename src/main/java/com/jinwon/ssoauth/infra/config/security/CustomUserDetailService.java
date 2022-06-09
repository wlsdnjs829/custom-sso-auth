package com.jinwon.ssoauth.infra.config.security;

import com.jinwon.ssoauth.domain.entity.user.User;
import com.jinwon.ssoauth.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증 서비스
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    private static final String USER_IS_NOT_EXISTS = "존재하지 않는 사용자입니다.";

    @Override
    public User loadUserByUsername(String name) {
        final User user = userRepository.findByUid(name)
                .orElseThrow(() -> new UsernameNotFoundException(USER_IS_NOT_EXISTS));
        detailsChecker.check(user);
        return user;
    }

    /**
     * 유효한 사용자 반환, 유효하지 않을 시 예외 처리
     *
     * @param userId 사용자 아이디
     * @param userPw 사용자 개인 코드
     * @throws IllegalArgumentException
     */
    public User validUserThrowIfInvalid(String userId, String userPw) {
        final User user = userRepository.findByUid(userId)
                .orElseThrow(IllegalArgumentException::new);

        final String password = user.getPassword();

        if (passwordEncoder.matches(userPw, password)) {
            return user;
        }

        throw new IllegalArgumentException();
    }

}
