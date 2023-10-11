package com.social.feed.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowUserResponseDto {
    private long userId;
    private String firstName;
    private String secondName;
    private String userType;
}
