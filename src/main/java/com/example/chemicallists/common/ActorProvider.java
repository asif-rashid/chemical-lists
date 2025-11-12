package com.example.chemicallists.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ActorProvider {

    private final SecurityProperties securityProperties;

    public ActorProvider(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return securityProperties.anonymousActor();
    }
}
