package org.ul.ciri.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@ConditionalOnProperty(name = "security.mode", havingValue = "api-key", matchIfMissing = true)
public class ApiKeySecurityConfig {

    @Bean
    public SecurityFilterChain apiKeyFilterChain(HttpSecurity http, ApiKeyProperties properties) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .addFilterBefore(new ApiKeyAuthenticationFilter(properties),
                        AbstractPreAuthenticatedProcessingFilter.class);
        return http.build();
    }
}
