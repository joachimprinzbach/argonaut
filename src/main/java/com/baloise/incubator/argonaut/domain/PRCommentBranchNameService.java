package com.baloise.incubator.argonaut.domain;

public interface PRCommentBranchNameService {

    String getBranchNameForPrCommentUrl(String baseRepoAPIUrl, int issueNr);
}
