package com.jinwon.ssoauth.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String API_NAME = "RPM_SSO_AUTH";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "Rpm Sso Auth API 명세서";

    private static final String JWT = "JWT";
    private static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";
    private static final String BEARER = "Bearer";

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group(API_NAME)
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(X_AUTH_TOKEN, getSecurityScheme()))
                .security(List.of(getSecurityRequirement()))
                .info(new Info().title(API_NAME)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION));
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                .scheme(BEARER)
                .bearerFormat(JWT)
                .in(SecurityScheme.In.HEADER)
                .name(X_AUTH_TOKEN);
    }

    private SecurityRequirement getSecurityRequirement() {
        return new SecurityRequirement().addList(X_AUTH_TOKEN);
    }

}
