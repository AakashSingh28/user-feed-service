package com.social.feed.interactors;

import com.social.feed.dtos.UserCommentRequestDto;
import com.social.feed.dtos.UserEventResponseDto;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.dtos.UserResponseDto;
import com.social.feed.entities.UserFollowings;
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

        UserPostResponseDto userPostResponseDto = new UserPostResponseDto("content", 2, "Bangalore", Collections.emptySet(), Collections.emptyMap());
        UserPostResponseDto[] mockPosts = { userPostResponseDto };

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
        // Mock the restTemplate to return null response
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(UserPostResponseDto[].class)))
                .thenReturn(null);

        UserFollowings userFollowing = new UserFollowings();
        userFollowing.setFollowingId(1l);
        userFollowing.setFollowerId(2l);

        FeedServiceException exception = assertThrows(FeedServiceException.class,
                () -> userPostServiceInteract.getPostsFromFollowingPeople(userFollowing));

        assertTrue(exception.getMessage().contains("2"));

        Mockito.verify(restTemplate, Mockito.times(1)).getForEntity(any(String.class), eq(UserPostResponseDto[].class));
    }

    @Test
    void testUpdatePostRankingOnUserLike() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Post ranking updated successfully", HttpStatus.OK));

        assertDoesNotThrow(() -> userPostServiceInteract.updatePostRankingOnUserLike("postId", 1L));
    }
    @Test
    void testUpdatePostRankingOnUserComment() {
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("postId");

        when(restTemplate.postForEntity(anyString(), eq(commentRequestDto), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Post ranking updated successfully", HttpStatus.OK));

        ResponseEntity<String> response = userPostServiceInteract.updatePostRankingOnUserComment(commentRequestDto);

        assertNotNull(response, "Response entity should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status code should be OK");
        assertEquals("Post ranking updated successfully", response.getBody(), "Response body should match the expected value");
    }
    @Test
    void testUpdateEventRankingOnUserLike() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Event ranking updated successfully", HttpStatus.OK));

        assertDoesNotThrow(() -> userPostServiceInteract.updateEventRankingOnUserLike(1L, "eventId"));
    }

    @Test
    void testGetEventsFromFollowingPeople() {
        UserEventResponseDto[] response = new UserEventResponseDto[1];
        UserEventResponseDto  userEventResponseDto= new UserEventResponseDto();

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
        userFollowing.setFollowingId(1l);
        userFollowing.setFollowerId(2l);

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
        userFollowing.setFollowingId(1l);
        userFollowing.setFollowerId(2l);

        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userPostServiceInteract.getEventsFromFollowingPeople(userFollowing));

        Mockito.verify(restTemplate, Mockito.times(1)).getForEntity(any(String.class), eq(UserEventResponseDto[].class));
    }

}
