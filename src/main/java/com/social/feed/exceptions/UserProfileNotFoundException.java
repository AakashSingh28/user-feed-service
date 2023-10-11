package com.social.feed.exceptions;

public class UserProfileNotFoundException extends RuntimeException {
    public UserProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserProfileNotFoundException(String message) {
        super(message);
    }
}
