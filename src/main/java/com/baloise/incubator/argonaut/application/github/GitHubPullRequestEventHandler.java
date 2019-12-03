package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestService;
import com.baloise.incubator.argonaut.infrastructure.github.ConditionalGitHub;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHEventPayload;
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
public class GitHubPullRequestEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);
    private static final String CLOSED = "closed";
    private static final String OPENED = "opened";

    private final PullRequestService pullRequestService;
    private final GitHub gitHub;

    public void handlePullRequestEvent(String payload) throws IOException {
        GHEventPayload.PullRequest ghPullRequest = gitHub.parseEventPayload(new StringReader(payload), GHEventPayload.PullRequest.class);
        switch (ghPullRequest.getAction()) {
            case CLOSED: {
                boolean isAlreadyMerged = ghPullRequest.getPullRequest().isMerged();
                if (isAlreadyMerged) {
                    // TODO Delete branch and preview environment -> Cleanup
                }
                LOGGER.info("PR CLOSED Event");
                break;
            }
            case OPENED: {
                PullRequest pullRequest = createPullRequest(ghPullRequest.getPullRequest());
                pullRequestService.createPullRequestComment(new PullRequestComment("This PR is managed by **[Argonaut](https://github.com/baloise-incubator/argonaut).** \n\n You can use the command `/ping` as pull request command to test the interaction in a comment. \n\n You can use the command `/deploy` to deploy this branch to it's preview environment (after build is successfull). \nYou can use the command `/promote` to promote this branch to production.", pullRequest));
                break;
            }
            default: {
                LOGGER.info("Unhandled GitHub Pull Request Event Action: {}", ghPullRequest.getAction());
            }
        }
    }

}
