package com.social.feed.interactors;

import com.social.feed.dtos.UserCommentRequestDto;
import com.social.feed.dtos.UserEventResponseDto;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.dtos.UserResponseDto;
import com.social.feed.entities.UserFollowings;
import com.social.feed.enums.PostType;
import com.social.feed.exceptions.FeedServiceException;
import com.social.feed.exceptions.UserServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPostServiceInteractTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserPostServiceInteract userPostServiceInteract;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPostsFromFollowingPeople() {
        UserFollowings userFollowing = new UserFollowings();
        userFollowing.setFollowerId(1L);

        UserPostResponseDto userPostResponseDto = new UserPostResponseDto("content", 2, "Bangalore", Collections.emptySet(), Collections.emptyMap(),false, PostType.TEXT.name());
        UserPostResponseDto[] mockPosts = {userPostResponseDto};

        when(restTemplate.getForEntity(anyString(), eq(UserPostResponseDto[].class)))
                .thenReturn(new ResponseEntity<>(mockPosts, HttpStatus.OK));

        List<UserResponseDto> posts = userPostServiceInteract.getPostsFromFollowingPeople(userFollowing);

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertTrue(posts.get(0) instanceof UserPostResponseDto);
        assertEquals(userPostResponseDto, posts.get(0));
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(UserPostResponseDto[].class));
    }


    @Test
    void testGetPostsFromFollowingPeople_Exception() {
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(UserPostResponseDto[].class)))
                .thenThrow(new FeedServiceException("Connection failed"));

        UserFollowings userFollowing = new UserFollowings();
        userFollowing.setFollowingId(1L);
        userFollowing.setFollowerId(2L);

        assertThrows(FeedServiceException.class, () -> {
            userPostServiceInteract.getPostsFromFollowingPeople(userFollowing);
        });

        Mockito.verify(restTemplate, Mockito.times(1)).getForEntity(any(String.class), eq(UserPostResponseDto[].class));
    }


    @Test
    void testUpdatePostRankingOnUserLike() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Post ranking updated successfully", HttpStatus.OK));

        assertDoesNotThrow(() -> userPostServiceInteract.updatePostRankingOnUserLike("postId", 1L));
    }
    @Test
    void testUpdatePostRankingOnUserLikeException() {
        when(restTemplate.postForEntity(
                Mockito.anyString(),
                Mockito.any(),
                eq(String.class)
        ))
                .thenThrow(UserServiceException.class);

        assertThrows(UserServiceException.class, () -> {
            userPostServiceInteract.updatePostRankingOnUserLike("100", 101L);
        });
    }


    @Test
    void testUpdatePostRankingOnUserComment() {
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("postId");

        when(restTemplate.postForEntity(anyString(), eq(commentRequestDto), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Post ranking updated successfully", HttpStatus.OK));

        userPostServiceInteract.updatePostRankingOnUserComment(commentRequestDto);

        Mockito.verify(restTemplate, Mockito.times(1)).postForEntity(anyString(), eq(commentRequestDto), eq(String.class));
    }
    @Test
    void testUpdatePostRankingOnUserCommentException() {
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setComment("My comment");
        commentRequestDto.setPostId("1001");
        commentRequestDto.setUserId(101L);
        when(restTemplate.postForEntity(Mockito.anyString(), any(), eq(String.class)))
                .thenThrow(UserServiceException.class);

        assertThrows(UserServiceException.class, () -> {
            userPostServiceInteract.updatePostRankingOnUserComment(commentRequestDto);
        });
    }

    @Test
    void testUpdateEventRankingOnUserLike() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Event ranking updated successfully", HttpStatus.OK));

        assertDoesNotThrow(() -> userPostServiceInteract.updateEventRankingOnUserLike(1L, "eventId"));
    }

    @Test
    void testUpdateEventRankingOnUserLikeException() {
        when(restTemplate.postForEntity(Mockito.anyString(), any(), eq(String.class)))
                .thenThrow(UserServiceException.class);

        assertThrows(UserServiceException.class, () -> {
            userPostServiceInteract.updateEventRankingOnUserLike(101L, "100");
        });
    }


    @Test
    void testGetEventsFromFollowingPeople() {
        UserEventResponseDto[] response = new UserEventResponseDto[1];
        UserEventResponseDto userEventResponseDto = new UserEventResponseDto();

        userEventResponseDto.setEventName("Sample Event Name");
        userEventResponseDto.setEventStartDate(new Date());
        userEventResponseDto.setEventEndDate(new Date());
        userEventResponseDto.setContent("Sample Event Content");
        userEventResponseDto.setPostScore(42);
        userEventResponseDto.setUserLocation("Sample Location");

        Set<String> usersLike = new HashSet<>();
        usersLike.add("1");
        usersLike.add("2");
        userEventResponseDto.setUsersLike(usersLike);


        Map<Long, List<String>> userAndComments = new HashMap<>();
        userAndComments.put(1L, Arrays.asList("Comment1", "Comment2"));
        userAndComments.put(2L, Arrays.asList("Comment3", "Comment4"));
        userEventResponseDto.setUserAndComments(userAndComments);

        response[0] = userEventResponseDto;

        Mockito.when(restTemplate.getForEntity(any(String.class), eq(UserEventResponseDto[].class)))
                .thenReturn(ResponseEntity.ok(response));

        UserFollowings userFollowing = new UserFollowings();
        userFollowing.setFollowingId(1L);
        userFollowing.setFollowerId(2L);

        List<UserResponseDto> userEventResponses = userPostServiceInteract.getEventsFromFollowingPeople(userFollowing);
        assertNotNull(userEventResponses);
        assertEquals(1, userEventResponses.size());

        Mockito.verify(restTemplate, Mockito.times(1)).getForEntity(any(String.class), eq(UserEventResponseDto[].class));
    }

    @Test
    void testGetEventsFromFollowingPeopleException() {
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(UserEventResponseDto[].class)))
                .thenThrow(new UserServiceException("Event not found"));

        UserFollowings userFollowing = new UserFollowings();
        userFollowing.setFollowingId(1L);
        userFollowing.setFollowerId(2L);

        assertThrows(UserServiceException.class,
                () -> userPostServiceInteract.getEventsFromFollowingPeople(userFollowing));

        Mockito.verify(restTemplate, Mockito.times(1)).getForEntity(any(String.class), eq(UserEventResponseDto[].class));
    }


}
