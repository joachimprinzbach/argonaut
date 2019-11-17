package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.GitServerKind;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class GitHubCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String gitServer = conditionContext.getEnvironment()
                .getProperty("argonaut.gitserver");
        return GitServerKind.GIT_HUB.getValue().equals(gitServer);
    }
}
