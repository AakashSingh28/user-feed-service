package com.social.feed.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateUserDetailsRequestDto {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String dateOfBirth;
    @NotNull
    private String emailId;
    @NotNull
    private String userLocation;
}
