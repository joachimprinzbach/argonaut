package com.baloise.incubator.argonaut;

public enum GitServerKind {

    BIT_BUCKET("bitbucket"),
    GIT_HUB("github");

    private String value;

    GitServerKind(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
