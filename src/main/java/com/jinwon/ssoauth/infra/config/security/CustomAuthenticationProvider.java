package com.jinwon.ssoauth.infra.config.security;

import com.jinwon.ssoauth.domain.entity.user.User;
import com.jinwon.ssoauth.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepo;

    @Override
    public Authentication authenticate(Authentication authentication) {
        final Optional<Authentication> authenticationOp = Optional.ofNullable(authentication);

        final String name = authenticationOp.map(Principal::getName)
                .orElse(StringUtils.EMPTY);

        final String password = authenticationOp.map(Authentication::getCredentials)
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);

        final User user = userRepo.findByUid(name)
                .orElseThrow(() -> new UsernameNotFoundException("user is not exists"));

        final String userPassword = user.getPassword();

        if (!passwordEncoder.matches(password, userPassword)) {
            throw new BadCredentialsException("password is not valid");
        }

        final Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

}