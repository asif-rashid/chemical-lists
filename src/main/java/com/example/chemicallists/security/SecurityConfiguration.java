package com.example.chemicallists.security;

import com.example.chemicallists.common.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityProperties securityProperties;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    public SecurityConfiguration(SecurityProperties securityProperties,
                                 ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
        this.securityProperties = securityProperties;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String mode = securityProperties.mode();

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if ("disabled".equalsIgnoreCase(mode)) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        } else {
            http.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.GET, "/lists", "/lists/*", "/lists/*/chemicals", "/actuator/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/lists/*/chemicals/*").permitAll()
                    .anyRequest().authenticated());
            if ("apikey".equalsIgnoreCase(mode)) {
                http.addFilterBefore(apiKeyAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);
            }
            if (securityProperties.enableOidc()) {
                http.oauth2ResourceServer(oauth2 -> oauth2.jwt());
            }
        }
        return http.build();
    }
}
