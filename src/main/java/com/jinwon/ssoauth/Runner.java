package com.jinwon.ssoauth;

import com.jinwon.ssoauth.domain.entity.User;
import com.jinwon.ssoauth.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class Runner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.save(
                User.builder()
                        .uid("user")
                        .password(passwordEncoder.encode("pass"))
                        .name("user")
                        .email("skyer9@gmail.com")
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build());
    }

}
