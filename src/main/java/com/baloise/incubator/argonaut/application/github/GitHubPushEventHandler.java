package com.baloise.incubator.argonaut.application.github;

import com.baloise.incubator.argonaut.infrastructure.github.ConditionalGitHub;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;

@ConditionalGitHub
@Component
@RequiredArgsConstructor
public class GitHubPushEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubWebhookRestController.class);

    private final GitHub gitHub;

    public void handlePullRequestEvent(String payload) throws IOException {
        GHEventPayload.Push push = gitHub.parseEventPayload(new StringReader(payload), GHEventPayload.Push.class);
        LOGGER.info("Received Push Event: {}", push.getHead());
    }

}
