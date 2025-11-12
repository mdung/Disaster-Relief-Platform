package com.relief.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "security.rate-limiting")
@Data
public class RateLimitProperties {
    private boolean enabled = true;
    private int defaultLimit = 100;
    private int defaultWindowSeconds = 60;
    private Map<String, Policy> roles = new HashMap<>(); // role -> policy
    private List<IpPolicy> ipPolicies; // CIDR based
    private Map<String, Policy> endpoints = new HashMap<>(); // path prefix -> policy

    @Data
    public static class Policy {
        private Integer limit; // requests per window
        private Integer windowSeconds;
    }

    @Data
    public static class IpPolicy {
        private String cidr;
        private Integer limit;
        private Integer windowSeconds;
    }
}





