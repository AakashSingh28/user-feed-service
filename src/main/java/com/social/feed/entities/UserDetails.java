package com.social.feed.entities;

import com.social.feed.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Generated;

import java.util.Date;
@Data
@Entity
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String emailId;
    private UserType userType;
    private Date createdOn;
    private Date updatedOn;

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
