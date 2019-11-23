package com.baloise.incubator.argonaut.infrastructure.github;

import com.baloise.incubator.argonaut.domain.PRCommentBranchNameService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalGitHub
public class GitHubPRCommentBranchNameService implements PRCommentBranchNameService {

    @Value("${argonaut.githubtoken}")
    private String apiToken;

    @Override
    public String getBranchNameForPrCommentUrl(String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor("", apiToken));
        ResponseEntity<GitHubIssueDto> forEntity =
                restTemplate.getForEntity(url, GitHubIssueDto.class);
        String prUrl = forEntity.getBody().getPull_request().getUrl();
        ResponseEntity<GitHubIssuePullRequestDto> forEntity2 =
                restTemplate.getForEntity(prUrl, GitHubIssuePullRequestDto.class);
        return forEntity2.getBody().getHead().getRef();
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


