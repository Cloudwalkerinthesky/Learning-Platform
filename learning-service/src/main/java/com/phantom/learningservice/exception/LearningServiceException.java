package com.phantom.learningservice.exception;

public class LearningServiceException extends RuntimeException{
    public LearningServiceException(String message){
        super(message);
    }
    public LearningServiceException(String message,Throwable cause){
        super(message, cause);
    }
}
