package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.infrastructure.github.ConditionalGitHub;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@ConditionalGitHub
@RequiredArgsConstructor
public class GitHubWebhookRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);
    private static final String GITHUB_EVENT_HEADER_KEY = "X-GitHub-Event";

    private final GitHubPullRequestEventHandler gitHubPullRequestEventHandler;
    private final GitHubPushEventHandler gitHubPushEventHandler;
    private final GitHubIssueCommentEventHandler gitHubIssueCommentEventHandler;


    @PostMapping(path = "webhook/github")
    public void handleGitHubWebhookEvent(@RequestBody String data, @RequestHeader(GITHUB_EVENT_HEADER_KEY) String githubEvent) throws IOException {
        Optional<GitHubEventType> gitHubEventType = GitHubEventType.fromEventName(githubEvent);
        if (gitHubEventType.isPresent()) {
            switch (gitHubEventType.get()) {
                case PULL_REQUEST: {
                    gitHubPullRequestEventHandler.handlePullRequestEvent(data);
                    break;
                }
                case PUSH: {
                    gitHubPushEventHandler.handlePullRequestEvent(data);
                    break;
                }
                case ISSUE_COMMENT: {
                    gitHubIssueCommentEventHandler.handlePullRequestEvent(data);
                    break;
                }
                default: {
                    LOGGER.warn("Unhandled GitHub Event Type: {}", gitHubEventType.get());
                }
            }
        } else {
            LOGGER.info("Ignored GitHub Event Type: {}", githubEvent);
        }
    }

    public static PullRequest createPullRequest(GHPullRequest pullRequest) {
        return new PullRequest(
                pullRequest.getNumber(),
                pullRequest.getRepository().getSvnUrl(),
                pullRequest.getRepository().getOwnerName(),
                pullRequest.getRepository().getName(),
                pullRequest.getHead().getRef(),
                pullRequest.getHead().getSha(),
                pullRequest.getHtmlUrl().toString()
        );
    }

}
