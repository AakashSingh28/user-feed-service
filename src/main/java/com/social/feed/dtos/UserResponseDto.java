package com.social.feed.dtos;

import com.social.feed.enums.PostType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserResponseDto {
    String getContent();
    int getPostScore();
    boolean isEventPost();
    String getPostType();
    String getUserLocation();
    Set<String> getUsersLike();
    Map<Long, List<String>> getUserAndComments();
}
