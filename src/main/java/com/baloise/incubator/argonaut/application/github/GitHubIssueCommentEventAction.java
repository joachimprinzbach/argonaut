package com.baloise.incubator.argonaut.application.github;

import java.util.Arrays;
import java.util.Optional;

public enum GitHubIssueCommentEventAction {

    CREATED("created"),
    EDITED("edited");

    private String action;

    GitHubIssueCommentEventAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static Optional<GitHubIssueCommentEventAction> fromActionName(String actionName) {
        return Arrays.stream(values())
                .filter(val -> val.getAction().equals(actionName))
                .findFirst();
    }
}
