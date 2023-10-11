package com.social.feed.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class UserFollowings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long followId;
    private Long followerId;
    private Long followingId;
    private Date createdOn;
    private Date updatedOn;
    private boolean isActive;

    private String followType;

    @PrePersist
    private void prePersist(){
        this.createdOn = new Date();
        this.updatedOn = new Date();
    }
    @PreUpdate
    private void preUpdate(){
        this.updatedOn = new Date();
    }
}
