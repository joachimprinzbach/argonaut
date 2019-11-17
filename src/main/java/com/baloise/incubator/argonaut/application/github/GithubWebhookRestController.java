package com.baloise.incubator.argonaut.application.github;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GithubWebhookRestController {

    @PostMapping(path = "webhook/github")
    public void handleGithubWebhookEvent(@RequestBody String data) {
        System.out.println(data);
    }
}
