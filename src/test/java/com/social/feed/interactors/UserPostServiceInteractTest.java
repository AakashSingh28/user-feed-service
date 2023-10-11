package com.social.feed.interactors;

import com.social.feed.dtos.UserPost;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.entities.UserFollowings;
import com.social.feed.exceptions.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        List<UserPostResponseDto> mockPosts = Arrays.asList(new UserPostResponseDto("", 2));

        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(mockPosts);

        List<UserPostResponseDto> posts = userPostServiceInteract.getPostsFromFollowingPeople(userFollowing);

        assertNotNull(posts, "The 'posts' list should not be null");
        assertEquals(1, posts.size(), "The 'posts' list should have one element");
        assertEquals(mockPosts, posts, "The 'posts' list should match the mocked list");
        verify(restTemplate, times(1)).getForObject(anyString(), eq(List.class));
    }




    @Test
    void testGetPostsFromFollowingPeople_PostNotFoundException() {

        UserFollowings userFollowing = new UserFollowings();
        userFollowing.setFollowerId(1L);
        when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
                .thenThrow(PostNotFoundException.class);

        // Act and Assert
        assertThrows(PostNotFoundException.class, () -> userPostServiceInteract.getPostsFromFollowingPeople(userFollowing));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(List.class));
    }

}
