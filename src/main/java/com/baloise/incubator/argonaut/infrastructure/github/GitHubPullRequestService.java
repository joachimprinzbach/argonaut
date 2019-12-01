package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.application.github.GitHubWebhookRestController;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestService;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalGitHub
public class GitHubPullRequestService implements PullRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);

    @Autowired
    private GitHub gitHub;

    @Override
    public void createPullRequestComment(PullRequestComment pullRequestComment) {
        LOGGER.info("Commenting Pull Request {}", pullRequestComment);
        PullRequest pullRequest = pullRequestComment.getPullRequest();
        try {
            gitHub.getRepository(pullRequest.getFullName()).getPullRequest(pullRequest.getId()).comment(pullRequestComment.getCommentText());
            LOGGER.info("Successfully commented on Pull Request");
        } catch (IOException e) {
            LOGGER.error("Error creating Pull Request comment: {}, Error: {}", pullRequestComment, e);
        }
    }

    @Override
    public String createPullRequest(PullRequest pullRequest) {
        try {
            GHPullRequest createdPr = gitHub.getRepository(pullRequest.getFullName()).createPullRequest("title", pullRequest.getHeadBranchName(), "master", "body");
            LOGGER.info("Successfully commented on Pull Request");
            return createdPr.getHtmlUrl().toString();
        } catch (IOException e) {
            LOGGER.error("Error creating Pull Request: {}, Error: {}", pullRequest, e);
        }
        return null;
    }

    @Override
    public void mergePullRequest(PullRequest pullRequest) {
        try {
            gitHub.getRepository(pullRequest.getFullName()).getPullRequest(pullRequest.getId()).merge("Merge changes");
            LOGGER.info("Successfully commented on Pull Request");
        } catch (IOException e) {
            LOGGER.error("Error merging Pull Request: {}, Error: {}", pullRequest, e);
        }
    }
}
