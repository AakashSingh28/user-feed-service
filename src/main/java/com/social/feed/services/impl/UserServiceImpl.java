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
import com.social.feed.exceptions.UserServiceException;
import com.social.feed.interactors.UserPostServiceInteract;
import com.social.feed.respositories.UserFollowingRepository;
import com.social.feed.respositories.UserRepository;
import com.social.feed.services.UserService;
import jakarta.transaction.Transactional;
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

    private final UserPostServiceInteract userPostServiceInteract;

    @Override
    public void createUser(CreateUserDetailsRequestDto createUserDetailsRequestDto) {
        try {
            UserDetails userDetails = new UserDetails();
            userDetails.setFirstName(createUserDetailsRequestDto.getFirstName());
            userDetails.setLastName(createUserDetailsRequestDto.getLastName());
            userDetails.setDateOfBirth(createUserDetailsRequestDto.getDateOfBirth());
            userDetails.setEmailId(createUserDetailsRequestDto.getEmailId());
            userDetails.setUserType(UserType.REGULAR);
            userDetails.setUserLocation(createUserDetailsRequestDto.getUserLocation());

            userRepository.save(userDetails);

            log.info("User created successfully: {}", userDetails);
        } catch (UserServiceException e) {
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
        } catch (UserServiceException e) {
            log.error("Error fetching potential users to be followed", e.getMessage());
            throw new UserServiceException("Error fetching potential users to be followed", e.getCause());
        }
    }

    @Override
    public void addUserSubscription(String followerId, String followingId, String followingType) {
        try {
            Optional<UserDetails> followerIdUserDetails = userRepository.findById(Long.parseLong(followerId));

            if(followerIdUserDetails.isEmpty()) {

                throw new UserNotFoundException("followerId "+ followerId + " is not found");
            }

            Optional<UserDetails> followingIdUserDetails = userRepository.findById(Long.parseLong(followingId));

            if(followingIdUserDetails.isEmpty()) {

             throw new UserNotFoundException("followingId "+ followingId + " is not found");
            }

            UserFollowings userFollowings = new UserFollowings();
            userFollowings.setFollowingId(Long.valueOf(followingId));
            userFollowings.setFollowerId(Long.valueOf(followerId));
            userFollowings.setFollowType(followingType);

            if (FollowType.valueOf(followingType) != FollowType.REGULAR) userFollowings.setActive(true);

            userFollowingRepository.save(userFollowings);

            log.info("User subscription requested successfully: {}", userFollowings);
        } catch (UserServiceException e) {
            log.error("Error while requested user subscription", e);
            throw new UserServiceException("Error while requested user subscription", e);
        }
    }

    @Override
    public UserProfileResponseDto getUserProfile(long userId) {
        try {
            Optional<UserDetails> userDetails = userRepository.findById(userId);

            if (userDetails.isEmpty()){
                throw new UserNotFoundException("User profile not found with ID: " + userId);
            }
                UserProfileResponseDto userProfileResponseDto = prepareUserProfileResponse(userDetails.get());
                log.info("Fetched user profile: {}", userProfileResponseDto);
                return userProfileResponseDto;

        }catch (UserServiceException e) {
            log.error("Error fetching user profile", e.getMessage());
            throw new UserServiceException("Error fetching user profile"+userId,e);
        }
    }

    @Override
    public void likePost(long userId, String postId) {
        Optional<UserDetails> userDetails = userRepository.findById(userId);
        if(userDetails.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        try {
            userPostServiceInteract.updatePostRankingOnUserLike(postId,userId);
        }catch (UserServiceException e){
            log.error("Exception while updating  user profile", e.getMessage());
            throw new UserServiceException("Exception while updating  user profile"+userId,e);
        }

    }
    @Override
    public void likeEvent(long userId, String eventId) {
        Optional<UserDetails> userDetails = userRepository.findById(userId);
        if (userDetails.isEmpty()){
            throw new UserNotFoundException("User not found");
        }

        try {
            userPostServiceInteract.updateEventRankingOnUserLike(userId,eventId);
        }
        catch (UserServiceException e){
            log.error("Exception while updating  user profile", e.getMessage());
            throw new UserServiceException("Exception while updating  user profile"+userId,e);
        }
    }
    @Transactional
    @Override
    public void acceptSubscription(String followerId, String followingId) {
        Optional<UserDetails> followerIdUserDetails = userRepository.findById(Long.parseLong(followerId));

        if(followerIdUserDetails.isEmpty()) {
            throw new UserNotFoundException("followerId "+ followerId + " is not found");
        }

        Optional<UserDetails> followingIdUserDetails = userRepository.findById(Long.parseLong(followingId));

        if(followingIdUserDetails.isEmpty()) {
            throw new UserNotFoundException("followingId "+ followingId + " is not found");
        }

        try {
            userFollowingRepository.updateIsActiveByFollowerIdAndFollowingId(
                    Long.parseLong(followerId), Long.parseLong(followingId), true);

            log.info("User subscription accepted successfully: {}", followerId);
        } catch (UserServiceException e) {
            log.error("Error accepting user subscription", e);
            throw new UserServiceException("Error accepting user subscription", e);
        }
    }


    @Override
    public void commentPost(UserCommentRequestDto commentRequestDto) {
        Optional<UserDetails> userDetails = userRepository.findById(commentRequestDto.getUserId());
        if(userDetails.isEmpty()) {
            throw new UserNotFoundException("User "+ commentRequestDto.getUserId() + " is not found");
        }
        try {
                userPostServiceInteract.updatePostRankingOnUserComment(commentRequestDto);
        }catch (UserServiceException e){
            log.error("Error while commenting on Post ", e);
            throw new UserServiceException("Error while commenting on Post ", e);
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
