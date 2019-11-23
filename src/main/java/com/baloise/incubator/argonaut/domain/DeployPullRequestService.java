package com.baloise.incubator.argonaut.domain;

public interface DeployPullRequestService {

    void deploy(String url, String fullName, String name, String newTag, String commentApiUrl);
}
