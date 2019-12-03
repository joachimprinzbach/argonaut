package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.application.github.GitHubWebhookRestController;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestService;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalGitHub
@RequiredArgsConstructor
public class GitHubPullRequestService implements PullRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);

    private final GitHub gitHub;

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
    public PullRequest createPullRequest(String repositoryFullName, String headBranchName) {
        LOGGER.info("Creating pr for Repository {} and branch: {}", repositoryFullName, headBranchName);
        try {
            GHPullRequest createdPr = gitHub.getRepository(repositoryFullName).createPullRequest(headBranchName, headBranchName, "master", "Deployment of " + headBranchName);
            LOGGER.info("Successfully commented on Pull Request");
            return GitHubWebhookRestController.createPullRequest(createdPr);
        } catch (IOException e) {
            LOGGER.error("Error creating Pull Request: {}, Error: {}", repositoryFullName, e);
        }
        return null;
    }

    @Override
    public void mergePullRequest(String repositoryFullName, int prId) {
        LOGGER.info("Merging PR with id {} ind repo {}", prId, repositoryFullName);
        try {
            gitHub.getRepository(repositoryFullName).getPullRequest(prId).merge("Merge changes");
            LOGGER.info("Successfully merged Pull Request");
        } catch (IOException e) {
            LOGGER.error("Error merging Pull Request: {}, Error: {}", repositoryFullName, e);
        }
    }

}
