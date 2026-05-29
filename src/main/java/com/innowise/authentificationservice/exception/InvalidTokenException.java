package com.innowise.authentificationservice.exception;

public class InvalidTokenException extends ServiceException{
    public InvalidTokenException(String message) {
        super(message);
    }
}