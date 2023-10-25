package com.social.feed.services.impl;

import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserCommentRequestDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.entities.UserDetails;
import com.social.feed.entities.UserFollowings;
import com.social.feed.enums.FollowType;
import com.social.feed.enums.UserType;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.interactors.UserPostServiceInteract;
import com.social.feed.respositories.UserFollowingRepository;
import com.social.feed.respositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    @Mock
    private UserPostServiceInteract userPostServiceInteract;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(UserDetails.class))).thenReturn(new UserDetails());

        CreateUserDetailsRequestDto createUserDetailsRequestDto = new CreateUserDetailsRequestDto();
        createUserDetailsRequestDto.setFirstName("Amit");
        createUserDetailsRequestDto.setLastName("Singh");
        createUserDetailsRequestDto.setDateOfBirth("1990-01-01");
        createUserDetailsRequestDto.setEmailId("Amit.singh@example.com");

        assertDoesNotThrow(() -> userService.createUser(createUserDetailsRequestDto));
    }

    @Test
    void testGetPotentialsUsersToBeFollowed() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                createUserDetails(1L, "Amit", "Singh", UserType.REGULAR),
                createUserDetails(2L, "Jane", "Singh", UserType.REGULAR)
        ));

        List<FollowUserResponseDto> potentialsUsers = userService.getPotentialsUsersToBeFollowed();

        assertNotNull(potentialsUsers);
        assertEquals(2, potentialsUsers.size());
    }

    @Test
    void testAddUserSubscription() {
        when(userFollowingRepository.save(any(UserFollowings.class))).thenReturn(new UserFollowings());
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new UserDetails()));
        assertDoesNotThrow(() -> userService.addUserSubscription("1", "2", FollowType.REGULAR.name()));
    }

    @Test
    void testGetUserProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUserDetails(1L, "Amit", "Singh", UserType.REGULAR)));
        UserProfileResponseDto userProfile = userService.getUserProfile(1L);

        assertNotNull(userProfile);
        assertEquals("Amit", userProfile.getFirstName());
        assertEquals(UserType.REGULAR.name(), userProfile.getUserType());
    }

    @Test
    void testGetUserProfile_UserProfileNotFoundException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfile(1L));
    }
    @Test
    void testAcceptSubscription() {
        doNothing().when(userFollowingRepository).updateIsActiveByFollowerIdAndFollowingId(1L, 2L, true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUserDetails(1L, "Amit", "Singh", UserType.REGULAR)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(createUserDetails(2L, "Jane", "Singh", UserType.REGULAR)));

        assertDoesNotThrow(() -> userService.acceptSubscription("1", "2"));
    }


    @Test
    void testAcceptSubscription_UserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.acceptSubscription("1", "2"));
    }

    @Test
    void testAcceptSubscription_UserNotFoundForFollower() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(createUserDetails(2L, "Jane", "Singh", UserType.REGULAR)));

        assertThrows(UserNotFoundException.class, () -> userService.acceptSubscription("1", "2"));
    }

    @Test
    void testAcceptSubscription_UserNotFoundForFollowing() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUserDetails(1L, "Amit", "Singh", UserType.REGULAR)));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.acceptSubscription("1", "2"));
    }

    @Test
    void testAcceptSubscription_FailureToUpdate() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(createUserDetails(1L, "Amit", "Singh", UserType.REGULAR)));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.acceptSubscription("1", "2"));
    }

    @Test
    void testLikePost() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUserDetails(1L, "Amit", "Singh", UserType.REGULAR)));
        doNothing().when(userPostServiceInteract).updatePostRankingOnUserLike("postId", 1L);

        assertDoesNotThrow(() -> userService.likePost(1L, "postId"));
    }
    @Test
    void testCommentPost() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUserDetails(1L, "Amit", "Singh", UserType.POLITICIAN)));
        UserCommentRequestDto commentRequestDto = new UserCommentRequestDto();
        commentRequestDto.setUserId(1L);
        commentRequestDto.setPostId("postId");

        assertDoesNotThrow(() -> userService.commentPost(commentRequestDto));
    }
    @Test
    void testLikeEvent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUserDetails(1L, "Amit", "Singh", UserType.CELEBRITY)));
        doNothing().when(userPostServiceInteract).updateEventRankingOnUserLike(1L, "eventId");

        assertDoesNotThrow(() -> userService.likeEvent(1L, "eventId"));
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
