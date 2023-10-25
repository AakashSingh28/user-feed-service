package com.social.feed.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostResponseDto implements UserResponseDto{

    private String content;

    private int postScore;

    private String userLocation;

    private Set<String> usersLike;

    private Map<Long, List<String>> userAndComments;

}
