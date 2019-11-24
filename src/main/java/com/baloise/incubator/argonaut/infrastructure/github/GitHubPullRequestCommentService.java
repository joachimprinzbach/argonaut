package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.application.github.GitHubWebhookRestController;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalGitHub
public class GitHubPullRequestCommentService implements PullRequestCommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void createPullRequestComment(PullRequestComment pullRequestComment, String url, int issueNr) {
        LOGGER.info("Commenting Pull Request {} nr {} with text {}", url, issueNr, pullRequestComment.getCommentText());
        ResponseEntity<Object> response =
                restTemplate.postForEntity(url + "/issues/" + issueNr + "/comments", new CreateCommentDto(pullRequestComment.getCommentText()), Object.class);
        LOGGER.info("Received response status code {}", response.getStatusCodeValue());
    }
}
