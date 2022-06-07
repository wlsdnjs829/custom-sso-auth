package com.jinwon.ssoauth.infra.config.security;

import com.jinwon.ssoauth.domain.entity.user.User;
import com.jinwon.ssoauth.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public UserDetails loadUserByUsername(String name) {
        final User user = userRepository.findByUid(name)
                .orElseThrow(() -> new UsernameNotFoundException("user is not exists"));
        detailsChecker.check(user);
        return user;
    }
}
