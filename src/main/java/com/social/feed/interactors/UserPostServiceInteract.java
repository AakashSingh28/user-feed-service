package com.social.feed.interactors;
import com.social.feed.dtos.UserCommentRequestDto;
import com.social.feed.dtos.UserEventResponseDto;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.dtos.UserResponseDto;
import com.social.feed.entities.UserFollowings;
import com.social.feed.exceptions.FeedServiceException;
import com.social.feed.exceptions.UserServiceException;
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

    public List<UserResponseDto> getPostsFromFollowingPeople(UserFollowings userFollowing) {
        String uri = postServiceBaseUrl + "/" + userFollowing.getFollowingId() + "/" +userFollowing.getFollowerId() ;

        try {
            ResponseEntity<UserPostResponseDto[]> responseEntity = restTemplate.getForEntity(uri, UserPostResponseDto[].class);
            if(responseEntity == null){
                throw new FeedServiceException("Error fetching user profile ");
            }
            List<UserResponseDto> userPostResponses = Arrays.asList(responseEntity.getBody());

            return userPostResponses;
        } catch (FeedServiceException ex) {
            log.info("Not able to fetch post data for {}", userFollowing.getFollowerId());
            throw new FeedServiceException("Error fetching user profile " + userFollowing.getFollowerId(), ex);
        }
    }

    public void updatePostRankingOnUserLike(String postId, long userId) {
        String updateRankingUri = postServiceBaseUrl + "post/like/"+userId+"/"+postId;

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(updateRankingUri, null, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Post ranking updated successfully");
            }
        } catch (UserServiceException ex) {
            log.error("Error updating post ranking", ex);
        }
    }

    public ResponseEntity<String> updatePostRankingOnUserComment(UserCommentRequestDto commentRequestDto) {
        String updateRankingUri = postServiceBaseUrl + "/comment";

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(updateRankingUri, commentRequestDto, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Post ranking updated successfully");
                return ResponseEntity.status(responseEntity.getStatusCode()).body("Post ranking updated successfully");
            } else {
                log.warn("Failed to update post ranking");
                return ResponseEntity.status(responseEntity.getStatusCode()).body("Failed to update post ranking");
            }
        } catch (UserServiceException ex) {
            log.error("Error updating post ranking", ex);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    public void updateEventRankingOnUserLike(long userId, String eventId) {
        String updateRankingUri = postServiceBaseUrl + "/event/like/"+userId+"/"+eventId;

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(updateRankingUri, null, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Event ranking updated successfully");
            }

        } catch (UserServiceException ex) {
            log.error("Error updating event ranking", ex);
        }
    }

    public List<UserResponseDto> getEventsFromFollowingPeople(UserFollowings userFollowing) {
        String uri = postServiceBaseUrl +"/event"+ "/" + userFollowing.getFollowingId() + "/" +userFollowing.getFollowerId() ;

        try {
            ResponseEntity<UserEventResponseDto[]> responseEntity = restTemplate.getForEntity(uri, UserEventResponseDto[].class);
            List<UserResponseDto> userEventResponses = Arrays.asList(responseEntity.getBody());

            return userEventResponses;
        } catch (UserServiceException ex) {
            log.info("Not able to fetch post data for {}", userFollowing.getFollowerId());
            throw new UserServiceException("Error fetching user profile " + userFollowing.getFollowerId(), ex);
        }
    }
}
