package com.baloise.incubator.argonaut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArgonautApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArgonautApplication.class, args);
    }

}
