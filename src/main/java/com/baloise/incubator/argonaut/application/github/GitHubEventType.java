package com.baloise.incubator.argonaut.application.github;

public enum GitHubEventType {

    PullRequestEvent("pull_request"),
    PushEvent("push");

    private String eventName;

    GitHubEventType(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }
}
