package com.baloise.incubator.argonaut.application.github;

public enum GitHubPullRequestEventAction {

    OPENED("opened"),
    CLOSED("closed");

    private String action;

    GitHubPullRequestEventAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
