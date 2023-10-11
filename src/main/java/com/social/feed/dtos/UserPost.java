package com.social.feed.dtos;

import com.social.feed.enums.PostType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UserPost {
    private String id;

    private long userId;
    private Date createdOn;
    private Date updatedOn;

    private Content content;


    @Data
    static class Content {

        @NotNull
        private PostType PostType;

        @NotNull
        private String value;
    }
}
