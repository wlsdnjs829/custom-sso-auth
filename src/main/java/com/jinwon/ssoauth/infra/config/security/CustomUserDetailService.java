package com.jinwon.ssoauth.infra.config.security;

import com.jinwon.ssoauth.domain.entity.profile.Profile;
import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import com.jinwon.ssoauth.infra.repository.ProfileRepository;
import com.jinwon.ssoauth.web.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 사용자 인증 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public Profile loadUserByUsername(String name) {
        final Profile profile = profileRepository.findByEmail(name)
                .orElseThrow(() -> new CustomException(TokenMessage.NOT_EXIST_USER));

        detailsChecker.check(profile);
        return profile;
    }

    /**
     * 유효한 프로필 반환, 유효하지 않을 시 예외 처리
     *
     * @param email 이메일
     * @param password 개인 코드
     */
    public Profile validProfileThrowIfInvalid(String email, String password) {
        final Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(TokenMessage.NOT_EXIST_USER));

        final String savedPassword = profile.getPassword();

        if (!passwordEncoder.matches(password, savedPassword)) {
            throw new CustomException(TokenMessage.NON_MATCH_USER_CODE);
        }

        return profile;
    }

}
