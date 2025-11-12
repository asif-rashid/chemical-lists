package com.example.chemicallists.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    /**
     * Security mode can be {@code disabled}, {@code apikey}, or {@code oidc}.
     */
    private String mode = "apikey";

    private String apiKeyHeaderName = "x-api-key";

    private String apiKeyValue = "local-dev-key";

    private String apiKeyPrincipal = "api-key";

    private boolean enableOidc = false;

    private String anonymousActor = "system";

    public String mode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String apiKeyHeaderName() {
        return apiKeyHeaderName;
    }

    public void setApiKeyHeaderName(String apiKeyHeaderName) {
        this.apiKeyHeaderName = apiKeyHeaderName;
    }

    public String apiKeyValue() {
        return apiKeyValue;
    }

    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public String apiKeyPrincipal() {
        return apiKeyPrincipal;
    }

    public void setApiKeyPrincipal(String apiKeyPrincipal) {
        this.apiKeyPrincipal = apiKeyPrincipal;
    }

    public boolean enableOidc() {
        return enableOidc;
    }

    public void setEnableOidc(boolean enableOidc) {
        this.enableOidc = enableOidc;
    }

    public String anonymousActor() {
        return anonymousActor;
    }

    public void setAnonymousActor(String anonymousActor) {
        this.anonymousActor = anonymousActor;
    }
}
