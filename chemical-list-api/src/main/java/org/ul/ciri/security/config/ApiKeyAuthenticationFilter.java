package org.ul.ciri.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-API-Key";

    private final ApiKeyProperties properties;

    public ApiKeyAuthenticationFilter(ApiKeyProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader(HEADER_NAME);
        if (token != null && !token.isBlank()) {
            for (Map.Entry<String, String> entry : properties.getKeys().entrySet()) {
                if (entry.getValue().equals(token)) {
                    SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthenticationToken(entry.getKey()));
                    break;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
