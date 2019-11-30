package com.baloise.incubator.argonaut;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
import com.baloise.incubator.argonaut.domain.PullRequest;
import com.baloise.incubator.argonaut.domain.PullRequestComment;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class ArgonautApplication {

    @Autowired
    private PullRequestCommentService pullRequestCommentService;

    @Autowired
    private GitHub gitHub;

    @Value("${argonaut.githubtoken}")
    private String apiToken;

    public static void main(String[] args) {
        SpringApplication.run(ArgonautApplication.class, args);
    }

    @Scheduled(fixedDelay = 10000000)
    public void bla() throws IOException {
        //System.out.println(gitHub.getMyself().getAllOrganizations());
        // deployPullRequestService.deploy("https://github.com/baloise-incubator/argonaut-deployment-configuration", "baloise-incubator/argonaut", "argonaut", "latest", "https://api.github.com/repos/baloise-incubator/argonaut/issues/1/comments");
        //PullRequest pullRequest = new PullRequest(1, "baloise-incubator", "argonaut");
        //pullRequestCommentService.createPullRequestComment(pullRequest.createNewPullRequestComment(LocalDateTime.now().toString()));
    }

}
