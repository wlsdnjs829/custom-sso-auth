package com.jinwon.ssoauth.infra.config;

import com.jinwon.ssoauth.infra.component.TokenRedisComponent;
import com.jinwon.ssoauth.infra.config.jwt.JwtAuthenticationFilter;
import com.jinwon.ssoauth.infra.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 시큐리티 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRedisComponent tokenRedisComponent;

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security
                .csrf(AbstractHttpConfigurer::disable)
                .headers(HeadersConfigurer::frameOptions)
                .headers(HeadersConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(SecurityConfig::customize)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, tokenRedisComponent),
                        UsernamePasswordAuthenticationFilter.class);
    }

    private static void customize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.
                                          ExpressionInterceptUrlRegistry registry) {
        registry.antMatchers("/oauth/**", "/h2-console/**", "/login", "/refresh-token")
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}