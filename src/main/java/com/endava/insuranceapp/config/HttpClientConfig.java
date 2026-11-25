package com.endava.insuranceapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient exchangeRateRestClient() {
        // Switch provider to Frankfurter
        return RestClient.builder()
                .baseUrl("https://api.frankfurter.dev")
                .build();
    }
}