package com.baloise.incubator.argonaut;

import com.baloise.incubator.argonaut.domain.PullRequestComment;
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

    public static void main(String[] args) {
        SpringApplication.run(ArgonautApplication.class, args);
    }

    @Scheduled(fixedDelay = 100000)
    public void bla() {
        commentService.createPullRequestComment(new PullRequestComment("blabb"), "https://github.com/baloise-incubator/argonaut-deployment-configuration");
    }

}
