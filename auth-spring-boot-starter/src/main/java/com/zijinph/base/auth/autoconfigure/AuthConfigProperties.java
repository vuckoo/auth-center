package com.zijinph.base.auth.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@ConfigurationProperties(
        prefix = AuthConfigProperties.PAGEHELPER_PREFIX
)
public class AuthConfigProperties {

    public static final String PAGEHELPER_PREFIX = "zauth";

    private Properties properties = new Properties();

    public AuthConfigProperties() {
    }

    public Properties getProperties() {
        return properties;
    }

    public String getClientUserMethod() {
        return properties.getProperty("clientUserMethod");
    }

    public void setClientUserMethod(String clientUserMethod) {
        properties.setProperty("clientUserMethod", clientUserMethod);
    }

    public String getAuthUrl() {
        return properties.getProperty("authUrl");
    }

    public void setAuthUrl(String authUrl) {
        properties.setProperty("authUrl", authUrl);
    }

}
