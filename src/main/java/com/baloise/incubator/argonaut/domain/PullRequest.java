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

    public String getFullName() {
        return organisation + "/" + repository;
    }

}
