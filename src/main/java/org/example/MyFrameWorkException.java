package org.example;

public class MyFrameWorkException extends RuntimeException{

    public MyFrameWorkException(String message) {
        super(message);
    }

    public MyFrameWorkException(Throwable cause) {
        super("exception not resolved", cause);
    }
}
