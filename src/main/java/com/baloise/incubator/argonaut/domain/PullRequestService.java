package com.baloise.incubator.argonaut.domain;

public interface PullRequestService {

    void createPullRequestComment(PullRequestComment pullRequestComment);

    PullRequest createPullRequest(String repositoryFullName, String headBranchName);

    void mergePullRequest(String repositoryFullName, int prId);
}
