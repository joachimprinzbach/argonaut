package com.baloise.incubator.argonaut.infrastructure.bitbucket;

import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.springframework.stereotype.Service;

@Service
@ConditionalBitBucket
public class BitBucketPullRequestCommentService implements PullRequestCommentService {

    @Override
    public void createPullRequestComment(PullRequestComment pullRequestComment, String commentApiUrl, int issueNr) {
        System.out.println("bitbucket");
    }
}
