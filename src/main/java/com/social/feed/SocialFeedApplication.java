package com.social.feed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SocialFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialFeedApplication.class, args);
    }

    @Bean
    public RestTemplate getTemplate(){
        return new RestTemplate();
    }
}
