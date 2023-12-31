package com.social.feed.controllers;

import com.social.feed.dtos.UserFeedsResponseDto;
import com.social.feed.exceptions.FeedServiceException;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.services.FeedService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedsController {

    private static final Logger log = LoggerFactory.getLogger(FeedsController.class);

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<List<UserFeedsResponseDto>> getFeeds(@RequestParam(name = "userId") String userId) {
        try {
            log.info("Fetching feeds for user with ID: {}", userId);

            List<UserFeedsResponseDto> feeds = feedService.getFeedsForTheUser(userId);

            log.info("Feeds fetched successfully");

            return ResponseEntity.ok(feeds);

        } catch (UserNotFoundException e) {
            log.warn("UserNotFoundException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        catch (FeedServiceException e) {
            log.warn("FeedServiceException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
}
