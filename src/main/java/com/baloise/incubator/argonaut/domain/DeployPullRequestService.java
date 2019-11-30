package com.baloise.incubator.argonaut.domain;

public interface DeployPullRequestService {

    void deploy(PullRequest pullRequest, String deploymentRepoUrl, String newImageTag);

    void promoteToProd(PullRequest pullRequest, String deploymentRepoUrl, String newImageTag);
}
