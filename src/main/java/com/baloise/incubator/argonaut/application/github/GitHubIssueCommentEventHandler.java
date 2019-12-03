package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestService;
import com.baloise.incubator.argonaut.infrastructure.github.ConditionalGitHub;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;

import static com.baloise.incubator.argonaut.application.github.GitHubWebhookRestController.createPullRequest;

@ConditionalGitHub
@Component
@RequiredArgsConstructor
public class GitHubIssueCommentEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);
    private static final String CREATED = "created";
    private static final String EDITED = "edited";

    private final PullRequestService pullRequestService;
    private final DeployPullRequestService deployPullRequestService;
    private final GitHub gitHub;

    public void handlePullRequestEvent(String payload) throws IOException {
        GHEventPayload.IssueComment issueComment = gitHub.parseEventPayload(new StringReader(payload), GHEventPayload.IssueComment.class);
        switch (issueComment.getAction()) {
            case CREATED: {
                String commentText = issueComment.getComment().getBody();
                GHPullRequest ghPullRequest = issueComment.getRepository().getPullRequest(issueComment.getIssue().getNumber());
                PullRequest pullRequest = createPullRequest(ghPullRequest);
                if ("/ping".startsWith(commentText)) {
                    pullRequestService.createPullRequestComment(new PullRequestComment("pong!", pullRequest));
                } else if ("/deploy".startsWith(commentText)) {
                    deployPullRequestService.deploy(pullRequest, false);
                } else if ("/promote".startsWith(commentText)) {
                    deployPullRequestService.deploy(pullRequest, false);
                } else {
                    LOGGER.info("Unhandled comment command: " + commentText);
                }
                break;
            }
            case EDITED: {
                LOGGER.info("Issue comment edited");
                break;
            }
            default: {
                LOGGER.warn("Unhandled GitHub Issue Comment Event Action: {}", issueComment.getAction());
            }
        }
    }

}
