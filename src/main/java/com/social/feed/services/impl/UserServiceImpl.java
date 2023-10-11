package com.social.feed.services.impl;

import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.entities.UserDetails;
import com.social.feed.entities.UserFollowings;
import com.social.feed.enums.FollowType;
import com.social.feed.enums.UserType;
import com.social.feed.exceptions.UserNotRegisterException;
import com.social.feed.exceptions.UserProfileNotFoundException;
import com.social.feed.respositories.UserFollowingRepository;
import com.social.feed.respositories.UserRepository;
import com.social.feed.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFollowingRepository userFollowingRepository;

    @Override
    public void createUser(CreateUserDetailsRequestDto createUserDetailsRequestDto) {
        try {
            UserDetails userDetails = new UserDetails();
            userDetails.setFirstName(createUserDetailsRequestDto.getFirstName());
            userDetails.setLastName(createUserDetailsRequestDto.getLastName());
            userDetails.setDateOfBirth(createUserDetailsRequestDto.getDateOfBirth());
            userDetails.setEmailId(createUserDetailsRequestDto.getEmailId());
            userDetails.setUserType(UserType.REGULAR);

            userRepository.save(userDetails);

            log.info("User created successfully: {}", userDetails);
        } catch (UserNotRegisterException e) {
            log.error("Error while creating user", e);
            throw new RuntimeException("Error while creating user", e);
        }
    }

    @Override
    public List<FollowUserResponseDto> getPotentialsUsersToBeFollowed() {
        try {
            List<UserDetails> userDetailsList = userRepository.findAll();

            List<FollowUserResponseDto> followUserResponseDtoList = userDetailsList.stream()
                    .map(this::buildFollowUserResponseDto)
                    .collect(Collectors.toList());

            log.info("Fetched potential users to be followed: {}", followUserResponseDtoList);
            return followUserResponseDtoList;
        } catch (Exception e) {
            log.error("Error fetching potential users to be followed", e);
            // You may choose to throw a custom exception or handle it as appropriate
            throw new RuntimeException("Error fetching potential users to be followed", e);
        }
    }

    @Override
    public void addUserSubscription(String followerId, String followingId, String followingType) {
        try {
            UserFollowings userFollowings = new UserFollowings();
            userFollowings.setFollowingId(Long.valueOf(followingId));
            userFollowings.setFollowerId(Long.valueOf(followerId));
            userFollowings.setFollowType(followingType);

            if (FollowType.valueOf(followingType) != FollowType.REGULAR) userFollowings.setActive(true);

            userFollowingRepository.save(userFollowings);

            log.info("User subscription added successfully: {}", userFollowings);
        } catch (Exception e) {
            log.error("Error adding user subscription", e);
            throw new RuntimeException("Error adding user subscription", e);
        }
    }

    @Override
    public UserProfileResponseDto getUserProfile(long userId) {
        try {
            Optional<UserDetails> userDetails = userRepository.findById(userId);

            if (userDetails.isPresent()) {
                UserProfileResponseDto userProfileResponseDto = prepareUserProfileResponse(userDetails.get());
                log.info("Fetched user profile: {}", userProfileResponseDto);
                return userProfileResponseDto;
            }
            throw new UserProfileNotFoundException("User profile not found with ID: " + userId);
        }catch (UserProfileNotFoundException e) {
            log.error("Error fetching user profile", e);
            throw new UserProfileNotFoundException("Error fetching user profile"+userId,e);
        }
    }

    private UserProfileResponseDto prepareUserProfileResponse(UserDetails userDetails) {
        return new UserProfileResponseDto(userDetails.getFirstName(), userDetails.getUserType().name());
    }

    private FollowUserResponseDto buildFollowUserResponseDto(UserDetails userDetails) {
        return new FollowUserResponseDto(
                userDetails.getUserId(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getUserType().name());
    }
}
