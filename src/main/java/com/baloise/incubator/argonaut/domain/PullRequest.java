package com.baloise.incubator.argonaut.domain;

import lombok.Data;

@Data
public class PullRequest {

    private final int id;
    private final String organisation;
    private final String repository;
    private String title;
    private String head;
    private String base;

    private PullRequestCommentService pullRequestCommentService;

    public void createNewPullRequest(String organisation, String repository, String title) {
        // TODO: Retrieve id instead of hard code
        PullRequest pullRequest = new PullRequest(1, organisation, repository);
    }

    public void createNewPullRequestComment(String comment) {
        PullRequestComment pullRequestComment = new PullRequestComment(comment, this);
        pullRequestCommentService.createPullRequestComment(pullRequestComment);
    }

    public String getFullName() {
        return organisation + "/" + repository;
    }


}
