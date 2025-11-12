package com.example.chemicallists.security;

import com.example.chemicallists.common.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    public ApiKeyAuthenticationFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"apikey".equalsIgnoreCase(securityProperties.mode())) {
            return true;
        }
        return HttpMethod.GET.matches(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerName = securityProperties.apiKeyHeaderName();
        String providedKey = request.getHeader(headerName);
        if (providedKey != null && providedKey.equals(securityProperties.apiKeyValue())) {
            AbstractAuthenticationToken authentication = new AbstractAuthenticationToken(
                    AuthorityUtils.createAuthorityList("ROLE_WRITER")) {
                @Override
                public Object getCredentials() {
                    return providedKey;
                }

                @Override
                public Object getPrincipal() {
                    return securityProperties.apiKeyPrincipal();
                }
            };
            authentication.setAuthenticated(true);
            authentication.setDetails(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
