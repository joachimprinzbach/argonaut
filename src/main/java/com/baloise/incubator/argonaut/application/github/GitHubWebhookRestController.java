package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class GitHubWebhookRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);
    private static final String GITHUB_EVENT_HEADER_KEY = "X-GitHub-Event";

    @Autowired
    private PullRequestCommentService commentService;

    @PostMapping(path = "webhook/github")
    public void handleGitHubWebhookEvent(@RequestBody String data, @RequestHeader(GITHUB_EVENT_HEADER_KEY) String githubEvent) {
        Optional<GitHubEventType> gitHubEventType = GitHubEventType.fromEventName(githubEvent);
        if (gitHubEventType.isPresent()) {
            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
            switch (gitHubEventType.get()) {
                case PULL_REQUEST: {
                    handlePullRequestEvent(jsonObject);
                    break;
                }
                case PUSH: {
                    handlePushEvent(jsonObject);
                    break;
                }
                case ISSUE_COMMENT: {
                    handleIssueCommentEvent(jsonObject);
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

    private void handlePullRequestEvent(JsonObject jsonObject) {
        String action = getActionElementValue(jsonObject);
        Optional<GitHubPullRequestEventAction> gitHubPullRequestEventAction = GitHubPullRequestEventAction.fromActionName(action);
        if (gitHubPullRequestEventAction.isPresent()) {
            switch (gitHubPullRequestEventAction.get()) {
                case CLOSED: {
                    System.out.println("PR CLOSED");
                    break;
                }
                case OPENED: {
                    System.out.println("PR OPENED");
                    break;
                }
                default: {
                    LOGGER.info("Unhandled GitHub Pull Request Event Action: {}", gitHubPullRequestEventAction.get());
                }
            }
        }
    }

    private void handlePushEvent(JsonObject jsonObject) {
        LOGGER.info("Received a push event");
    }

    private void handleIssueCommentEvent(JsonObject jsonObject) {
        String action = getActionElementValue(jsonObject);
        Optional<GitHubIssueCommentEventAction> gitHubIssueCommentEventAction = GitHubIssueCommentEventAction.fromActionName(action);
        if (gitHubIssueCommentEventAction.isPresent()) {
            switch (gitHubIssueCommentEventAction.get()) {
                case CREATED: {
                    System.out.println("Issue comment created");
                    String asString = jsonObject.get("comment").getAsJsonObject().get("body").getAsString();
                    if (asString.startsWith("/ping")) {
                        commentService.createPullRequestComment(new PullRequestComment("pong!"));
                    }
                    break;
                }
                case EDITED: {
                    System.out.println("Issue comment edited");
                    break;
                }
                default: {
                    LOGGER.info("Unhandled GitHub Issue Comment Event Action: {}", gitHubIssueCommentEventAction.get());
                }
            }
        }
    }

    private String getActionElementValue(JsonObject jsonObject) {
        return jsonObject.get("action").getAsString();
    }
}
