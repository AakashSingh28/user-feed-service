package com.social.feed.services.impl;

import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.entities.UserDetails;
import com.social.feed.entities.UserFollowings;
import com.social.feed.enums.FollowType;
import com.social.feed.enums.UserType;
import com.social.feed.exceptions.UserProfileNotFoundException;
import com.social.feed.respositories.UserFollowingRepository;
import com.social.feed.respositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFollowingRepository userFollowingRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(UserDetails.class))).thenReturn(new UserDetails()); // Adjust the return type accordingly

        CreateUserDetailsRequestDto createUserDetailsRequestDto = new CreateUserDetailsRequestDto();
        createUserDetailsRequestDto.setFirstName("John");
        createUserDetailsRequestDto.setLastName("Doe");
        createUserDetailsRequestDto.setDateOfBirth("1990-01-01");
        createUserDetailsRequestDto.setEmailId("john.doe@example.com");

        assertDoesNotThrow(() -> userService.createUser(createUserDetailsRequestDto));
    }

    @Test
    void testGetPotentialsUsersToBeFollowed() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                createUserDetails(1L, "John", "Doe", UserType.REGULAR),
                createUserDetails(2L, "Jane", "Doe", UserType.REGULAR)
        ));

        List<FollowUserResponseDto> potentialsUsers = userService.getPotentialsUsersToBeFollowed();

        assertNotNull(potentialsUsers);
        assertEquals(2, potentialsUsers.size());
    }

    @Test
    void testAddUserSubscription() {
        when(userFollowingRepository.save(any(UserFollowings.class))).thenReturn(new UserFollowings());

        assertDoesNotThrow(() -> userService.addUserSubscription("1", "2", FollowType.REGULAR.name()));
    }

    @Test
    void testGetUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUserDetails(1L, "John", "Doe", UserType.REGULAR)));
        UserProfileResponseDto userProfile = userService.getUserProfile(1L);

        assertNotNull(userProfile);
        assertEquals("John", userProfile.getFirstName());
        assertEquals(UserType.REGULAR.name(), userProfile.getUserType());
    }

    @Test
    void testGetUserProfile_UserProfileNotFoundException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class, () -> userService.getUserProfile(1L));
    }

    private UserDetails createUserDetails(long userId, String firstName, String lastName, UserType userType) {
        UserDetails userDetails = new UserDetails();
        userDetails.setUserId(userId);
        userDetails.setFirstName(firstName);
        userDetails.setLastName(lastName);
        userDetails.setUserType(userType);
        return userDetails;
    }
}
