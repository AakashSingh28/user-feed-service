package com.social.feed.interactors;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.entities.UserFollowings;
import com.social.feed.exceptions.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPostServiceInteract {

    @Value("${application.post-service.url}")
    private String postServiceBaseUrl;

    private final RestTemplate restTemplate;

    public List<UserPostResponseDto> getPostsFromFollowingPeople(UserFollowings userFollowing) {
        String uri = postServiceBaseUrl + "/" + userFollowing.getFollowerId() + "/1";

        try {
            ResponseEntity<UserPostResponseDto[]> responseEntity = restTemplate.getForEntity(uri, UserPostResponseDto[].class);
            List<UserPostResponseDto> userPostResponses = Arrays.asList(responseEntity.getBody());

            return userPostResponses;
        } catch (PostNotFoundException ex) {
            log.info("Not able to fetch post data for {}", userFollowing.getFollowerId());
            throw new PostNotFoundException("Error fetching user profile " + userFollowing.getFollowerId(), ex);
        }
    }

}
