package com.social.feed.dtos;

import com.social.feed.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventResponseDto implements  UserResponseDto{

    private String eventName;

    private Date eventStartDate;

    private Date eventEndDate;

    private String content;

    private int postScore;

    private String userLocation;

    private Set<String> usersLike;

    private Map<Long, List<String>> userAndComments;

    private boolean isEventPost;

    private String postType;
}
