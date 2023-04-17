package com.example.oblakscraper.exceptions;

public class PasswordsDoNotMatchException extends Exception {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}