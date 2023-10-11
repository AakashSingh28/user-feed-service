package com.social.feed.exceptions;

public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
