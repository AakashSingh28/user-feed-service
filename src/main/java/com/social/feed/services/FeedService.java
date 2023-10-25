package com.social.feed.services;

import com.social.feed.dtos.UserFeedsResponseDto;
import com.social.feed.exceptions.FeedServiceException;
import com.social.feed.exceptions.UserNotFoundException;

import java.util.List;

public interface FeedService {

    List<UserFeedsResponseDto> getFeedsForTheUser(String userId) throws FeedServiceException, UserNotFoundException;
}
