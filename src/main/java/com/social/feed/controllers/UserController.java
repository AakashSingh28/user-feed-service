package com.social.feed.controllers;

import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.exceptions.UserNotRegisterException;
import com.social.feed.exceptions.UserProfileNotFoundException;
import com.social.feed.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        } catch (UserNotRegisterException e) {
            log.error("Error registering user: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }

    @GetMapping("/userSuggestions")
    public ResponseEntity<List<FollowUserResponseDto>> getUsers() {
        try {
            List<FollowUserResponseDto> users = userService.getPotentialsUsersToBeFollowed();
            return ResponseEntity.ok(users);
        } catch (UserProfileNotFoundException e) {
            log.error("Error fetching user suggestions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/subscribe/{followerId}/{followingId}/{followingType}")
    public ResponseEntity<String> addSubscription(@PathVariable String followerId,
                                                  @PathVariable String followingId,
                                                  @PathVariable String followingType) {
        try {
            userService.addUserSubscription(followerId, followingId, followingType);
            return ResponseEntity.status(HttpStatus.OK).body("Subscription added successfully");
        } catch (UserProfileNotFoundException e) {
            log.error("Error adding subscription: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add subscription");
        }
    }

    @GetMapping(value = "/potentialsUsersToBeFollowed")
    public ResponseEntity<List<FollowUserResponseDto>> getPotentialsUsersToBeFollowed() {
        try {
            List<FollowUserResponseDto> users = userService.getPotentialsUsersToBeFollowed();
            return ResponseEntity.ok(users);
        } catch (UserProfileNotFoundException e) {
            log.error("Error fetching potential users to be followed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getProfile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@RequestParam(name = "userId") long userId) {
        try {
            UserProfileResponseDto userProfile = userService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (UserProfileNotFoundException e) {
            log.error("Error fetching user profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
