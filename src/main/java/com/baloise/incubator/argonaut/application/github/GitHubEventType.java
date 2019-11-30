package com.baloise.incubator.argonaut.application.github;

import java.util.Arrays;
import java.util.Optional;

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

    public static Optional<GitHubEventType> fromEventName(String eventName) {
        return Arrays.stream(values())
                .filter(val -> val.getEventName().equals(eventName))
                .findFirst();
    }
}
