package com.baloise.incubator.argonaut.domain;

import lombok.Data;

@Data
public class PullRequest {

    private final int id;
    private final String baseRepoGitUrl;
    private final String organisation;
    private final String repository;
    private final String headBranchName;
    private final String headCommitSHA;
    private final String prWebUrl;

    public String getFullName() {
        return organisation + "/" + repository;
    }

}
