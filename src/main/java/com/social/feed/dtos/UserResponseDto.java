package com.social.feed.dtos;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserResponseDto {
    String getContent();
    int getPostScore();
    String getUserLocation();
    Set<String> getUsersLike();
    Map<Long, List<String>> getUserAndComments();
}
