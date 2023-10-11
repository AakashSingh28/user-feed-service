package com.social.feed.dtos;

import com.social.feed.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponseDto {

    private String firstName;
    private String userType;
}
