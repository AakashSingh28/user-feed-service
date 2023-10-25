package com.social.feed.services;


import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserCommentRequestDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.exceptions.UserServiceException;

import java.util.List;

public interface UserService {

    public void createUser(CreateUserDetailsRequestDto createUserDetailsRequestDto) throws UserServiceException;

    List<FollowUserResponseDto> getPotentialsUsersToBeFollowed();

    void addUserSubscription(String followerId, String followingId, String followingType) throws UserNotFoundException,UserServiceException;

    UserProfileResponseDto getUserProfile(long userId) throws UserNotFoundException;

    void likePost(long userId, String postId) throws UserNotFoundException;
    void commentPost(UserCommentRequestDto commentRequestDto);

    void likeEvent(long userId, String eventId);

    void acceptSubscription(String followerId, String followingId);
}
