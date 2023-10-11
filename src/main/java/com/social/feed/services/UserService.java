package com.social.feed.services;


import com.social.feed.dtos.CreateUserDetailsRequestDto;
import com.social.feed.dtos.FollowUserResponseDto;
import com.social.feed.dtos.UserProfileResponseDto;
import com.social.feed.enums.FollowType;

import java.util.List;

public interface UserService {

    public void createUser(CreateUserDetailsRequestDto createUserDetailsRequestDto);

    List<FollowUserResponseDto> getPotentialsUsersToBeFollowed();

    void addUserSubscription(String followerId, String followingId, String followingType);

    UserProfileResponseDto getUserProfile(long userId);
}
