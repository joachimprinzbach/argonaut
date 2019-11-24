package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
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
    private PullRequestCommentService pullRequestCommentService;

    @Autowired
    private DeployPullRequestService deployPullRequestService;

    @PostMapping(path = "webhook/github")
    public void handleGitHubWebhookEvent(@RequestBody String data, @RequestHeader(GITHUB_EVENT_HEADER_KEY) String githubEvent) {
        Optional<GitHubEventType> gitHubEventType = GitHubEventType.fromEventName(githubEvent);
        if (gitHubEventType.isPresent()) {
            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
            LOGGER.info("Received Event of type {}", gitHubEventType.get());
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
                    LOGGER.info("PR CLOSED Event");
                    break;
                }
                case OPENED: {
                    LOGGER.info("PR OPENED Event");
                    break;
                }
                default: {
                    LOGGER.info("Unhandled GitHub Pull Request Event Action: {}", gitHubPullRequestEventAction.get());
                }
            }
        }
    }

    private void handlePushEvent(JsonObject jsonObject) {
        LOGGER.info("REPO PUSH Event");
    }

    private void handleIssueCommentEvent(JsonObject jsonObject) {
        String action = getActionElementValue(jsonObject);
        Optional<GitHubIssueCommentEventAction> gitHubIssueCommentEventAction = GitHubIssueCommentEventAction.fromActionName(action);
        if (gitHubIssueCommentEventAction.isPresent()) {
            switch (gitHubIssueCommentEventAction.get()) {
                case CREATED: {
                    JsonObject comment = jsonObject.get("comment").getAsJsonObject();
                    String commentText = comment.get("body").getAsString();
                    JsonObject issue = jsonObject.get("issue").getAsJsonObject();
                    String baseRepoAPIUrl = issue.get("repository_url").getAsString();
                    int issueNr = issue.get("number").getAsInt();
                    if (commentText.startsWith("/ping")) {
                        pullRequestCommentService.createPullRequestComment(new PullRequestComment("pong!"), baseRepoAPIUrl, issueNr);
                    } else {
                        String deployText = "/deploy ";
                        if (commentText.startsWith(deployText)) {
                            JsonObject repository = jsonObject.get("repository").getAsJsonObject();
                            String repoName = repository.get("name").getAsString();
                            String repoUrl = repository.get("svn_url").getAsString();
                            String repoFullName = repository.get("full_name").getAsString();
                            String tag = commentText.substring(commentText.indexOf(deployText) + deployText.length());
                            deployPullRequestService.deploy(repoUrl + "-deployment-configuration", repoFullName, repoName, tag, baseRepoAPIUrl, issueNr);
                        }
                    }
                    break;
                }
                case EDITED: {
                    LOGGER.info("Issue comment edited");
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
