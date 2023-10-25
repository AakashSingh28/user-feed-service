package com.social.feed.controllers;

import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserCommentRequestDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.exceptions.UserServiceException;
import com.social.feed.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> registerUser(@RequestBody @Valid CreateUserDetailsRequestDto createUserDetailsRequestDto) {
        try {
            userService.createUser(createUserDetailsRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User successfully registered");
        } catch (UserServiceException e) {
            log.error("Error registering user: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }


    @GetMapping(value = "/subscribe/{followerId}/{followingId}/{followingType}")
    public ResponseEntity<String> addSubscription(@PathVariable String followerId,
                                                  @PathVariable String followingId,
                                                  @PathVariable String followingType) {
        try {
            userService.addUserSubscription(followerId, followingId, followingType);
            return ResponseEntity.status(HttpStatus.OK).body("Subscription added successfully");
        } catch (UserNotFoundException e) {
            log.error("Error adding subscription: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to add subscription");
        } catch (UserServiceException e) {
            log.error("Error adding subscription: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add subscription");
        }
    }

    @PutMapping(value = "/acceptSubscription/{followerId}/{followingId}")
    public ResponseEntity<String> acceptSubscription(@PathVariable String followerId,
                                                  @PathVariable String followingId ){
        try {
            userService.acceptSubscription(followerId, followingId);
            return ResponseEntity.status(HttpStatus.OK).body("Subscription accepted  successfully");
        } catch (UserNotFoundException e) {
            log.error("Error adding subscription: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }catch (UserServiceException e) {
            log.error("Error adding subscription: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to accept subscription");
        }
    }

    @GetMapping(value = "/potentialsUsersToBeFollowed")
    public ResponseEntity<List<FollowUserResponseDto>> getPotentialsUsersToBeFollowed() {
        try {
            List<FollowUserResponseDto> users = userService.getPotentialsUsersToBeFollowed();
            return ResponseEntity.ok(users);
        } catch (UserServiceException e) {
            log.error("Error fetching potential users to be followed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.EMPTY_LIST);
        }
    }

    @GetMapping("/getProfile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@RequestParam(name = "userId") long userId) {
        try {
            UserProfileResponseDto userProfile = userService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);

        } catch (UserNotFoundException e) {
            log.error("Error fetching user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (UserServiceException e) {
            log.error("Error fetching user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/post/like")
    public ResponseEntity<String> likePost(
            @RequestParam(name = "userId") long userId,
            @RequestParam(name = "postId") String postId) {
        try {

            userService.likePost(userId, postId);

            return ResponseEntity.status(HttpStatus.OK).body("User liked the post successfully");

        } catch (UserNotFoundException e) {
            log.error("Error fetching user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (UserServiceException e) {
            log.error("Error fetching user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/event/like")
    public ResponseEntity<String> likeEvent(
            @RequestParam(name = "userId") long userId,
            @RequestParam(name = "eventId") String eventId) {
        try {
            userService.likeEvent(userId, eventId);
            return ResponseEntity.status(HttpStatus.OK).body("User liked the post successfully");
        } catch (UserNotFoundException e) {
            log.error("Error liking post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }catch (UserServiceException e) {
            log.error("Error liking post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to like post");
        }
    }

    @PutMapping("/comment")
    public ResponseEntity<String> commentPost(@RequestBody @Valid UserCommentRequestDto commentRequestDto) {
        try {
            userService.commentPost(commentRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body("User commented on the post successfully");
        } catch (UserNotFoundException e) {
            log.error("User not found {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }catch (UserServiceException e) {
            log.error("Error commenting on the post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to comment on the post");
        }
    }


}
