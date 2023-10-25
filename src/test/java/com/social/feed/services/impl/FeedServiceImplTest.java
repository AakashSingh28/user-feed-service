package com.social.feed.services.impl;

import com.social.feed.dtos.UserEventResponseDto;
import com.social.feed.dtos.UserFeedsResponseDto;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.dtos.UserResponseDto;
import com.social.feed.entities.UserDetails;
import com.social.feed.entities.UserFollowings;
import com.social.feed.enums.UserType;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.interactors.UserPostServiceInteract;
import com.social.feed.respositories.UserFollowingRepository;
import com.social.feed.respositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FeedServiceImplTest {

    @Mock
    private UserFollowingRepository userFollowingRepository;

    @Mock
    private UserPostServiceInteract userPostServiceInteract;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFeedsForTheUser_Success() {
        UserDetails userDetails = getUserDetails(UserType.REGULAR);

        when(userRepository.findById(Long.valueOf("123"))).thenReturn(Optional.of(userDetails));

        when(userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, 123L))
                .thenReturn(Collections.singletonList(new UserFollowings()));

        UserPostResponseDto userPostResponseDto1 = new UserPostResponseDto("content", 2, "Bangalore", Collections.emptySet(), Collections.emptyMap());
        UserPostResponseDto userPostResponseDto2 = new UserPostResponseDto("content", 2, "Patna", Collections.emptySet(), Collections.emptyMap());
        UserPostResponseDto userPostResponseDto3 = new UserPostResponseDto("content", 0, "Pune", Collections.emptySet(), Collections.emptyMap());
        UserPostResponseDto userPostResponseDto4 = new UserPostResponseDto("content", 0, "Pune", Collections.emptySet(), Collections.emptyMap());
        UserPostResponseDto userPostResponseDto5 = new UserPostResponseDto("content", 2, null, Collections.emptySet(), Collections.emptyMap());
        UserEventResponseDto userEventResponseDto1 =
                new UserEventResponseDto("Event Name", new Date(), new Date(), "Event Content", 42, "Indore", Collections.emptySet(), Collections.emptyMap());
        UserEventResponseDto userEventResponseDto2 = new UserEventResponseDto("Event Name", new Date(), new Date(), "Event Content", 10, "Bhopal", Collections.emptySet(), Collections.emptyMap());

        when(userPostServiceInteract.getPostsFromFollowingPeople(any(UserFollowings.class)))
                .thenReturn(List.of(userPostResponseDto1, userPostResponseDto2,userPostResponseDto3,userPostResponseDto4,userPostResponseDto5));

        when(userPostServiceInteract.getEventsFromFollowingPeople(any(UserFollowings.class)))
                .thenReturn(List.of(userEventResponseDto1, userEventResponseDto2));

        List<UserFeedsResponseDto> feeds = feedService.getFeedsForTheUser("123");

        assertNotNull(feeds);
        assertTrue(feeds.isEmpty());
    }

    @Test
    void testGetFeedsForTheUser_FeedServiceException() {
        when(userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, 1L))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> feedService.getFeedsForTheUser("1"));
    }

    @Test
    void testGetFeedsForTheUser_NoUserFound() {
        when(userRepository.findById(Long.valueOf("123"))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> feedService.getFeedsForTheUser("123"));
    }

    @Test
    void testGetFeedsForTheUser_NoUserFollowings() {
        UserDetails userDetails = getUserDetails(UserType.REGULAR);
        when(userRepository.findById(Long.valueOf("123"))).thenReturn(Optional.of(userDetails));
        when(userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, 123L))
                .thenReturn(Collections.emptyList());

        List<UserFeedsResponseDto> feeds = feedService.getFeedsForTheUser("123");

        assertNotNull(feeds);
        assertTrue(feeds.isEmpty());
    }

    @Test
    void testGetFeedsForTheUser_NoPostsOrEvents() {
        UserDetails userDetails = getUserDetails(UserType.POLITICIAN);
        when(userRepository.findById(Long.valueOf("123"))).thenReturn(Optional.of(userDetails));
        when(userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, 123L))
                .thenReturn(Collections.singletonList(new UserFollowings()));

        // Simulate no posts or events from userPostServiceInteract
        when(userPostServiceInteract.getPostsFromFollowingPeople(any(UserFollowings.class)))
                .thenReturn(Collections.emptyList());

        when(userPostServiceInteract.getEventsFromFollowingPeople(any(UserFollowings.class)))
                .thenReturn(Collections.emptyList());

        List<UserFeedsResponseDto> feeds = feedService.getFeedsForTheUser("123");

        assertNotNull(feeds);
        assertTrue(feeds.isEmpty());
    }

    @Test
    void testGetFeedsForTheUser_LimitFeeds() {
        UserDetails userDetails = getUserDetails(UserType.CELEBRITY);
        feedService.feedLimit=10;
        when(userRepository.findById(Long.valueOf("123"))).thenReturn(Optional.of(userDetails));
        when(userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, 123L))
                .thenReturn(Collections.singletonList(new UserFollowings()));

        // Mock more posts and events to exceed the feedLimit
        List<UserResponseDto> userPostResponseDtos = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            userPostResponseDtos.add(new UserPostResponseDto("content", i, "Bangalore", Collections.emptySet(), Collections.emptyMap()));
        }

        when(userPostServiceInteract.getPostsFromFollowingPeople(any(UserFollowings.class)))
                .thenReturn(userPostResponseDtos);

        when(userPostServiceInteract.getEventsFromFollowingPeople(any(UserFollowings.class)))
                .thenReturn(userPostResponseDtos);

        List<UserFeedsResponseDto> feeds = feedService.getFeedsForTheUser("123");

        assertNotNull(feeds);
        assertEquals(10, feeds.size());
    }

    private UserDetails getUserDetails(UserType type) {
        UserDetails userDetails = new UserDetails();
        userDetails.setUserId(123L);
        userDetails.setFirstName("Rahul");
        userDetails.setLastName("patel");
        userDetails.setDateOfBirth("1990-01-01");
        userDetails.setEmailId("Rahul.patel@example.com");
        userDetails.setUserType(type);
        userDetails.setUserLocation("Bangalore");
        userDetails.setCreatedOn(new Date());
        userDetails.setUpdatedOn(new Date());
        return userDetails;
    }
}
