package com.social.feed.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserCommentRequestDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.exceptions.UserServiceException;
import com.social.feed.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testRegisterUserException() throws Exception {
        CreateUserDetailsRequestDto createUserDetailsRequestDto = new CreateUserDetailsRequestDto();
        createUserDetailsRequestDto.setFirstName("Amit");
        createUserDetailsRequestDto.setLastName("singh");
        createUserDetailsRequestDto.setDateOfBirth("1990-01-01");
        createUserDetailsRequestDto.setEmailId("Amit.singh@example.com");

        doThrow(UserServiceException.class).when(userService).createUser(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users")
                        .content(asJsonString(createUserDetailsRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testRegisterUser() throws Exception {
        CreateUserDetailsRequestDto createUserDetailsRequestDto = new CreateUserDetailsRequestDto();
        createUserDetailsRequestDto.setFirstName("Amit");
        createUserDetailsRequestDto.setLastName("singh");
        createUserDetailsRequestDto.setDateOfBirth("1990-01-01");
        createUserDetailsRequestDto.setEmailId("Amit.singh@example.com");


        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users")
                        .content(asJsonString(createUserDetailsRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    void testRegisterUserForUserNotRegisterException() throws Exception {
        CreateUserDetailsRequestDto createUserDetailsRequestDto = new CreateUserDetailsRequestDto();
        createUserDetailsRequestDto.setFirstName("Amit");
        createUserDetailsRequestDto.setLastName("singh");
        createUserDetailsRequestDto.setDateOfBirth("1990-01-01");
        createUserDetailsRequestDto.setEmailId("Amit.singh@example.com");

        doThrow(UserServiceException.class).when(userService).createUser(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users")
                        .content(asJsonString(createUserDetailsRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testAddSubscription_UserProfileNotFoundException() throws Exception {
        doThrow(UserNotFoundException.class)
                .when(userService)
                .addUserSubscription(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/subscribe/1/2/REGULAR")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());

        doThrow(UserServiceException.class)
                .when(userService)
                .addUserSubscription(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/subscribe/1/2/REGULAR")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }
    @Test
    void testAddSubscription() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/subscribe/1/2/REGULAR")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testGetPotentialsUsersToBeFollowed() throws Exception {
        when(userService.getPotentialsUsersToBeFollowed())
                .thenReturn(Arrays.asList(
                        new FollowUserResponseDto(1,"testUser1","lastName1","CELEBRITY"),
                        new FollowUserResponseDto(1,"testUser2","lastName2","EVENT"),
                        new FollowUserResponseDto(2,"testUser3","lastName3","POLITICIAN")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/potentialsUsersToBeFollowed")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testGetPotentialsUsersToBeFollowedException() throws Exception {
        doThrow(UserServiceException.class).when(userService).getPotentialsUsersToBeFollowed();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/potentialsUsersToBeFollowed")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void testGetUserProfile_Success() throws Exception {
        UserProfileResponseDto userProfileResponseDto = new UserProfileResponseDto("John", "REGULAR");
        when(userService.getUserProfile(Mockito.anyLong())).thenReturn(userProfileResponseDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/getProfile?userId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userType").value("REGULAR"))
                .andDo(print());
    }

    @Test
    void testGetUserProfile_UserProfileNotFoundException() throws Exception {
        when(userService.getUserProfile(Mockito.anyLong()))
                .thenThrow(new UserNotFoundException("User profile not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/getProfile?userId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    void testAcceptSubscription() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/acceptSubscription/1/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testGetUserProfile_Exception() throws Exception {
        when(userService.getUserProfile(Mockito.anyLong()))
                .thenThrow(new UserServiceException("User profile not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/getProfile?userId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testGetUsers() throws Exception {
        when(userService.getPotentialsUsersToBeFollowed()).thenThrow(new UserNotFoundException("Error fetching user suggestions"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/userSuggestions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    void testAcceptSubscription_UserNotFoundException() throws Exception {
        doThrow(UserNotFoundException.class)
                .when(userService)
                .acceptSubscription(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/acceptSubscription/1/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());

        doThrow(UserServiceException.class)
                .when(userService)
                .acceptSubscription(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/acceptSubscription/1/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testLikePost_UserNotFoundException() throws Exception {
        doThrow(UserNotFoundException.class)
                .when(userService)
                .likePost(Mockito.anyLong(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/post/like?userId=1&postId=123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());

        doThrow(UserServiceException.class)
                .when(userService)
                .likePost(Mockito.anyLong(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/post/like?userId=1&postId=123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testLikePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/post/like?userId=1&postId=123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testLikeEvent_UserServiceException() throws Exception {
        doThrow(UserServiceException.class)
                .when(userService)
                .likeEvent(Mockito.anyLong(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/event/like?userId=1&eventId=456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testLikeEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/event/like?userId=1&eventId=456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testCommentPost_UserNotFoundException() throws Exception {
        doThrow(UserNotFoundException.class)
                .when(userService)
                .commentPost(Mockito.any());

        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("123");
        commentRequestDto.setComment("Test comment");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/comment")
                        .content(asJsonString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }
    @Test
    void testCommentPost() throws Exception {
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("123");
        commentRequestDto.setComment("Test comment");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/comment")
                        .content(asJsonString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testCommentPost_UserServiceException() throws Exception {
        doThrow(UserServiceException.class)
                .when(userService)
                .commentPost(Mockito.any());
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("123");
        commentRequestDto.setComment("Test comment");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/comment")
                        .content(asJsonString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testLikeEvent_UserNotFoundException() throws Exception {
        doThrow(UserNotFoundException.class)
                .when(userService)
                .likeEvent(Mockito.anyLong(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/users/event/like?userId=1&eventId=456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }


    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
