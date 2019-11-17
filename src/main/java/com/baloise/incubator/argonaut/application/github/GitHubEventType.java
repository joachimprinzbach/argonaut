package com.baloise.incubator.argonaut.application.github;

public enum GitHubEventType {

    PULL_REQUEST("pull_request"),
    ISSUE_COMMENT("issue_comment"),
    PUSH("push");

    private String eventName;

    GitHubEventType(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }
}
