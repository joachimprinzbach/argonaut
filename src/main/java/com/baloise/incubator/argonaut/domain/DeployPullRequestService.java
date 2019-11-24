package com.baloise.incubator.argonaut.domain;

public interface DeployPullRequestService {

    void deploy(String deploymentRepoUrl, String fullName, String name, String newTag, String commentApiUrl, int issueNr);
}
