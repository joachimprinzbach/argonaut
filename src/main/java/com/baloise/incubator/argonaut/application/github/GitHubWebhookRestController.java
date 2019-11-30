package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import com.baloise.incubator.argonaut.infrastructure.github.ConditionalGitHub;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

@RestController
@ConditionalGitHub
public class GitHubWebhookRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);
    private static final String GITHUB_EVENT_HEADER_KEY = "X-GitHub-Event";

    @Autowired
    private PullRequestCommentService pullRequestCommentService;

    @Autowired
    private DeployPullRequestService deployPullRequestService;

    @Autowired
    private GitHub gitHub;

    @PostMapping(path = "webhook/github")
    public void handleGitHubWebhookEvent(@RequestBody String data, @RequestHeader(GITHUB_EVENT_HEADER_KEY) String githubEvent) throws IOException {
        Optional<GitHubEventType> gitHubEventType = GitHubEventType.fromEventName(githubEvent);
        if (gitHubEventType.isPresent()) {
            LOGGER.info("Received Event of type {}", gitHubEventType.get());
            switch (gitHubEventType.get()) {
                case PULL_REQUEST: {
                    GHEventPayload.PullRequest pullRequest = gitHub.parseEventPayload(new StringReader(data), GHEventPayload.PullRequest.class);
                    handlePullRequestEvent(pullRequest);
                    break;
                }
                case PUSH: {
                    GHEventPayload.Push push = gitHub.parseEventPayload(new StringReader(data), GHEventPayload.Push.class);
                    handlePushEvent(push);
                    break;
                }
                case ISSUE_COMMENT: {
                    GHEventPayload.IssueComment issueComment = gitHub.parseEventPayload(new StringReader(data), GHEventPayload.IssueComment.class);
                    handleIssueCommentEvent(issueComment);
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

    private void handlePullRequestEvent(GHEventPayload.PullRequest ghPullRequest) {
        switch (ghPullRequest.getAction()) {
            case "closed": {
                LOGGER.info("PR CLOSED Event");
                break;
            }
            case "opened": {
                PullRequest pullRequest = createPullRequest(ghPullRequest.getNumber(), ghPullRequest.getRepository(), ghPullRequest.getPullRequest());
                pullRequestCommentService.createPullRequestComment(new PullRequestComment("This PR is managed by **[Argonaut](https://github.com/baloise-incubator/argonaut).** \n\n You can use the command `/ping` as pull request command to test the interaction in a comment. \n\n You can use the command `/deploy` to deploy this branch to it's preview environment (after build is successfull). \nYou can use the command `/promote` to promote this branch to production.", pullRequest));
                LOGGER.info("PR OPENED Event");
                break;
            }
            default: {
                LOGGER.info("Unhandled GitHub Pull Request Event Action: {}", ghPullRequest.getAction());
            }
        }
    }

    private void handlePushEvent(GHEventPayload.Push push) {
        LOGGER.info("REPO PUSH Event");
    }

    private void handleIssueCommentEvent(GHEventPayload.IssueComment issueComment) throws IOException {
        switch (issueComment.getAction()) {
            case "created": {
                String commentText = issueComment.getComment().getBody();
                GHRepository repository = issueComment.getRepository();
                GHPullRequest ghPullRequest = repository.getPullRequest(issueComment.getIssue().getNumber());
                PullRequest pullRequest = createPullRequest(issueComment.getIssue().getNumber(), repository, ghPullRequest);
                String deployText = "/deploy ";
                String promoteText = "/promote ";
                if (commentText.startsWith("/ping")) {
                    pullRequestCommentService.createPullRequestComment(new PullRequestComment("pong!", pullRequest));
                } else if (commentText.startsWith(deployText)) {
                    deployPullRequestService.deploy(pullRequest);
                } else if (commentText.startsWith(promoteText)) {
                    deployPullRequestService.promoteToProd(pullRequest);
                }
                break;
            }
            case "edited": {
                LOGGER.info("Issue comment edited");
                break;
            }
            default: {
                LOGGER.warn("Unhandled GitHub Issue Comment Event Action: {}", issueComment.getAction());
            }
        }
    }

    private PullRequest createPullRequest(int id, GHRepository repository, GHPullRequest pullRequest) {
        return new PullRequest(id, repository.getSvnUrl(), repository.getOwnerName(), repository.getName(), pullRequest.getHead().getRef(), pullRequest.getHead().getSha());
    }

}
