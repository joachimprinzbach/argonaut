package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.kohsuke.github.GHEventPayload;
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
                    PullRequest pullRequest = new PullRequest(ghPullRequest.getNumber(), ghPullRequest.getRepository().getOwnerName(), ghPullRequest.getRepository().getName());
                    pullRequestCommentService.createPullRequestComment(new PullRequestComment("[APPROVALNOTIFIER] This PR is **NOT APPROVED**\\n\\nThis pull-request has been approved by:\\nTo fully approve this pull request, please assign additional approvers.\\nWe suggest the following additional approver: **joachimprinzbach**\\n\\nIf they are not already assigned, you can assign the PR to them by writing `/assign @joachimprinzbach` in a comment when ready.\\n\\nThe full list of commands accepted by this bot can be found [here](dead.link).\\n\\nThe pull request process is described [here](also-dead-link)\\n\\n<details open>\\nNeeds approval from an approver in each of these files:\\n\\n- **[OWNERS](dead.link)**\\n\\nApprovers can indicate their approval by writing `/approve` in a comment\\nApprovers can cancel approval by writing `/approve cancel` in a comment\\n</details>\\n<!-- META={\\\"approvers\\\":[\\\"joachimprinzbach\\\"]} -->\"", pullRequest));
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

    private void handleIssueCommentEvent(GHEventPayload.IssueComment issueComment) {
        switch (issueComment.getAction()) {
            case "created": {
                String commentText = issueComment.getComment().getBody();
                GHRepository repository = issueComment.getRepository();
                PullRequest pullRequest = new PullRequest(issueComment.getIssue().getNumber(), repository.getOwnerName(), repository.getName());
                if (commentText.startsWith("/ping")) {
                    pullRequestCommentService.createPullRequestComment(new PullRequestComment("pong!", pullRequest));
                } else {
                    String deployText = "/deploy ";
                    if (commentText.startsWith(deployText)) {
                        String repoUrl = repository.getSvnUrl();
                        String tag = commentText.substring(commentText.indexOf(deployText) + deployText.length());
                        deployPullRequestService.deploy(pullRequest, repoUrl + "-deployment-configuration", tag);
                    }
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

    private String getActionElementValue(JsonObject jsonObject) {
        return jsonObject.get("action").getAsString();
    }
}
