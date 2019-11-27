package com.baloise.incubator.argonaut.domain;

import org.springframework.context.ApplicationEvent;

public class PullRequestCommentCreatedEvent extends ApplicationEvent {

    public PullRequestCommentCreatedEvent(PullRequestComment source) {
        super(source);
    }
}
