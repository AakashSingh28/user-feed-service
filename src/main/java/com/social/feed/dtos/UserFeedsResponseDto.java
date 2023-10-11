package com.social.feed.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserFeedsResponseDto {

    @Data
    static class PostComments{
        List<String> comments;
    }

    private String post;
    private String postedBy;
    private String postedOn;
    private int postLikes;
    private int postDislikes;
    private List<PostComments> postCommentsList;
}
