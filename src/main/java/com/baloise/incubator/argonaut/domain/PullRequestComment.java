package com.baloise.incubator.argonaut.domain;

import lombok.Value;

@Value
public class PullRequestComment {

    private final String commentText;
    private final PullRequest pullRequest;
}
