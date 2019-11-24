package com.baloise.incubator.argonaut.domain;

public interface PullRequestCommentService {

    void createPullRequestComment(PullRequestComment pullRequestComment, String commentApiUrl, int issueNr);
}
