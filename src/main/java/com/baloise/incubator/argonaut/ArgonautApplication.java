package com.baloise.incubator.argonaut;

import com.baloise.incubator.argonaut.domain.DeployPullRequestService;
import com.baloise.incubator.argonaut.domain.PullRequestCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class ArgonautApplication {

    @Autowired
    private PullRequestCommentService commentService;

    @Autowired
    private DeployPullRequestService deployPullRequestService;


    public static void main(String[] args) {
        SpringApplication.run(ArgonautApplication.class, args);
    }

    @Scheduled(fixedDelay = 10000000)
    public void bla() {
        // deployPullRequestService.deploy("https://github.com/baloise-incubator/argonaut-deployment-configuration", "baloise-incubator/argonaut", "argonaut", "latest", "https://api.github.com/repos/baloise-incubator/argonaut/issues/1/comments");
        //commentService.createPullRequestComment(new PullRequestComment("blabb"), "https://api.github.com/repos/baloise-incubator/argonaut/issues/1/comments");
    }

}
