package com.baloise.incubator.argonaut.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class PullRequest {

    @Autowired
    private PullRequestCommentService pullRequestCommentService;

    public void comment(PullRequestComment pullRequestComment) {
        pullRequestCommentService.createPullRequestComment(pullRequestComment);
    }
}
