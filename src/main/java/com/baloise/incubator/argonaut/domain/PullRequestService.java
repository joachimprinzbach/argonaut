package com.baloise.incubator.argonaut.domain;

public interface PullRequestService {

    void createPullRequestComment(PullRequestComment pullRequestComment);

    String createPullRequest(PullRequest pullRequest);

    void mergePullRequest(PullRequest pullRequest);
}
