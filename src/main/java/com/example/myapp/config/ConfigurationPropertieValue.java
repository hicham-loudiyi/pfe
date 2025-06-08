package com.example.myapp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ConfigurationPropertieValue {
    @Value("${clmBaseUrl}")
    private String clmBaseUrl;
    @Value("${clmModelName}")
    private String clmModelName;
    @Value("${timeout}")
    private int timeout;
    @Value("${temperature}")
    private double temperature;
    @Value("${emBaseUrl}")
    private String emBaseUrl;
    @Value("${emModelName}")
    private String emModelName;
    @Value("${crMaxResults}")
    private int crMaxResults;
    @Value("${crMinScore}")
    private double crMinScore;
}
