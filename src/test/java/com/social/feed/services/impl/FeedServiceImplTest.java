package com.social.feed.services.impl;

import com.social.feed.dtos.UserFeedsResponseDto;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.entities.UserFollowings;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.interactors.UserPostServiceInteract;
import com.social.feed.respositories.UserFollowingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FeedServiceImplTest {

    @Mock
    private UserFollowingRepository userFollowingRepository;

    @Mock
    private UserPostServiceInteract userPostServiceInteract;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFeedsForTheUser_Success() {
        when(userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, 1L))
                .thenReturn(Collections.singletonList(new UserFollowings()));

        when(userPostServiceInteract.getPostsFromFollowingPeople(any(UserFollowings.class)))
                .thenReturn(Collections.singletonList(new UserPostResponseDto("TEXT",2)));

        List<UserFeedsResponseDto> feeds = feedService.getFeedsForTheUser("1");

        assertNotNull(feeds);
        assertFalse(feeds.isEmpty());
    }


    @Test
    void testGetFeedsForTheUser_FeedServiceException() {
        when(userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, 1L))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> feedService.getFeedsForTheUser("1"));
    }

}
