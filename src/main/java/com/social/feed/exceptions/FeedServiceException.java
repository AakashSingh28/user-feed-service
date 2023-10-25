package com.social.feed.exceptions;

public class FeedServiceException extends RuntimeException {

    public FeedServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeedServiceException(String message) {
        super(message);
    }
}
