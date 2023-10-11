package com.social.feed.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.exceptions.UserNotRegisterException;
import com.social.feed.exceptions.UserProfileNotFoundException;
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
    void testRegisterUser() throws Exception {
        CreateUserDetailsRequestDto createUserDetailsRequestDto = new CreateUserDetailsRequestDto();
        createUserDetailsRequestDto.setFirstName("John");
        createUserDetailsRequestDto.setLastName("Doe");
        createUserDetailsRequestDto.setDateOfBirth("1990-01-01");
        createUserDetailsRequestDto.setEmailId("john.doe@example.com");

        doThrow(UserNotRegisterException.class).when(userService).createUser(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/users")
                        .content(asJsonString(createUserDetailsRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testRegisterUserForUserNotRegisterException() throws Exception {
        CreateUserDetailsRequestDto createUserDetailsRequestDto = new CreateUserDetailsRequestDto();
        createUserDetailsRequestDto.setFirstName("John");
        createUserDetailsRequestDto.setLastName("Doe");
        createUserDetailsRequestDto.setDateOfBirth("1990-01-01");
        createUserDetailsRequestDto.setEmailId("john.doe@example.com");

        doThrow(UserNotRegisterException.class).when(userService).createUser(Mockito.any());

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
        doThrow(UserProfileNotFoundException.class)
                .when(userService)
                .addUserSubscription(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/subscribe/1/2/REGULAR")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
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
                .thenThrow(new UserProfileNotFoundException("User profile not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/getProfile?userId=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void testGetUsers() throws Exception {
        when(userService.getPotentialsUsersToBeFollowed()).thenThrow(new UserProfileNotFoundException("Error fetching user suggestions"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/userSuggestions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
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
