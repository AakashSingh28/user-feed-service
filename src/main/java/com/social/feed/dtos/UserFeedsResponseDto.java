package com.social.feed.dtos;

import lombok.Data;

@Data
public class UserFeedsResponseDto {
    private UserResponseDto feed;

    public UserResponseDto getFeed() {
        return feed;
    }

}

