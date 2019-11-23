package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalGitHub
public class GitHubPullRequestCommentService implements PullRequestCommentService {

    @Value("${argonaut.githubtoken}")
    private String apiToken;

    @Override
    public void createPullRequestComment(PullRequestComment pullRequestComment, String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor("", apiToken));
        ResponseEntity<Object> forEntity =
                restTemplate.postForEntity(url, new CreateCommentDto(pullRequestComment.getCommentText()), Object.class);
        System.out.println(forEntity);
    }
}
