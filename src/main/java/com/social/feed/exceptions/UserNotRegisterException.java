package com.social.feed.exceptions;

public class UserNotRegisterException extends RuntimeException {
    public UserNotRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
