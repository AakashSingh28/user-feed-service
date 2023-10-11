package com.social.feed.services;

import com.social.feed.dtos.UserFeedsResponseDto;

import java.util.List;

public interface FeedService {

    List<UserFeedsResponseDto> getFeedsForTheUser(String userId);
}
