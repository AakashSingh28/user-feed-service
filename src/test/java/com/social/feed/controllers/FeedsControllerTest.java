package com.social.feed.controllers;

import com.social.feed.dtos.UserFeedsResponseDto;
import com.social.feed.exceptions.FeedServiceException;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.respositories.UserRepository;
import com.social.feed.services.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class FeedsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FeedService feedService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedsController feedsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(feedsController).build();
    }

    @Test
    void testGetFeedsSuccess() throws Exception {

        when(feedService.getFeedsForTheUser(anyString()))
                .thenReturn(Collections.singletonList(new UserFeedsResponseDto()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/feeds")
                        .param("userId", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    void testGetFeedsUserNotFoundException() throws Exception {
        when(feedService.getFeedsForTheUser(anyString()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/feeds")
                        .param("userId", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    void testGetFeedsForFeedServiceException() throws Exception {
        when(feedService.getFeedsForTheUser(anyString()))
                .thenThrow(FeedServiceException.class);

       mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/feeds")
                        .param("userId", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
    }
}
