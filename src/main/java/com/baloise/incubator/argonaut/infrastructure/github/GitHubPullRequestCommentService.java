package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.springframework.stereotype.Service;

@Service
@ConditionalGitHub
public class GitHubPullRequestCommentService implements PullRequestCommentService {

    @Override
    public void createPullRequestComment(PullRequestComment pullRequestComment) {
        System.out.println("github");
    }
}
