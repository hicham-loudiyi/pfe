package com.example.myapp.exceptions;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String message) { super(message); }
}
