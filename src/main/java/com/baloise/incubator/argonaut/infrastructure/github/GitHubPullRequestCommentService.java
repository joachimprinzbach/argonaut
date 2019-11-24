package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.application.github.GitHubWebhookRestController;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalGitHub
public class GitHubPullRequestCommentService implements PullRequestCommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);

    @Value("${argonaut.githubtoken}")
    private String apiToken;

    @Override
    public void createPullRequestComment(PullRequestComment pullRequestComment, String url) {
        LOGGER.info("Commenting Pull Request {} with text {}", url, pullRequestComment.getCommentText());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor("", apiToken));
        ResponseEntity<Object> response =
                restTemplate.postForEntity(url, new CreateCommentDto(pullRequestComment.getCommentText()), Object.class);
        LOGGER.info("Received response status code {}", response.getStatusCodeValue());
    }
}
