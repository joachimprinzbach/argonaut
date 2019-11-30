package com.baloise.incubator.argonaut.infrastructure.bitbucket;

import com.baloise.incubator.argonaut.GitServerKind;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class BitBucketCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String gitServer = conditionContext.getEnvironment()
                .getProperty("argonaut.gitserver");
        return GitServerKind.BIT_BUCKET.getValue().equals(gitServer);
    }
}
