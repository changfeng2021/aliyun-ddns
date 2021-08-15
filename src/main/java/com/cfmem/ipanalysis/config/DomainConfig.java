package com.cfmem.ipanalysis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "ddns")
public class DomainConfig {

    private String regionId;
    private String accessKeyId;
    private String secret;
    private Map<String, List<String>> domain;
}
