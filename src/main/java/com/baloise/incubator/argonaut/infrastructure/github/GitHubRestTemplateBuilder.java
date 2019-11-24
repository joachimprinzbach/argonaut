package com.baloise.incubator.argonaut.infrastructure.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ConditionalGitHub
@Component
public class GitHubRestTemplateBuilder {

    @Value("${argonaut.githubtoken}")
    private String apiToken;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .interceptors(new BasicAuthenticationInterceptor("", apiToken))
                .build();
    }
}
