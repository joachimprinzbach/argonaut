package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.domain.PRCommentBranchNameService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalGitHub
public class GitHubPRCommentBranchNameService implements PRCommentBranchNameService {

    @Autowired
    private GitHub gitHub;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getBranchNameForPrCommentUrl(String baseRepoAPIUrl, int issueNr) {
        ResponseEntity<GitHubIssueDto> issueResponse =
                restTemplate.getForEntity(baseRepoAPIUrl + "/issues/" + issueNr, GitHubIssueDto.class);
        String prUrl = issueResponse.getBody().getPull_request().getUrl();
        ResponseEntity<GitHubIssuePullRequestDto> issuePullRequestResponse =
                restTemplate.getForEntity(prUrl, GitHubIssuePullRequestDto.class);
        return issuePullRequestResponse.getBody().getHead().getRef();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GitHubIssueDto {
    private GitHubIssuePullRequestUrlDto pull_request;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GitHubIssuePullRequestUrlDto {
    private String url;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GitHubIssuePullRequestDto {
    private GitHubHeadDto head;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GitHubHeadDto {
    private String ref;
}


