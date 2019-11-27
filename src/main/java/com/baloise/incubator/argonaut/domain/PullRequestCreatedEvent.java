package com.baloise.incubator.argonaut.domain;

import org.springframework.context.ApplicationEvent;

public class PullRequestCreatedEvent extends ApplicationEvent {

    public PullRequestCreatedEvent(PullRequest source) {
        super(source);
    }
}
