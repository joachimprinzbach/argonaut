package com.baloise.incubator.argonaut.infrastructure.github;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ConditionalGitHub
@Component
public class GitHubApiConfiguration {

    @Value("${argonaut.gittoken}")
    private String apiToken;

    @Bean
    public GitHub createGitHub() throws IOException {
        return new GitHubBuilder().withOAuthToken(apiToken).build();
    }
}
