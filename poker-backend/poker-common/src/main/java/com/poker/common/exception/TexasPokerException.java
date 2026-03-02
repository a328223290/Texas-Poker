package com.poker.common.exception;

import lombok.Getter;

/**
 * Custom business exception
 */
@Getter
public class TexasPokerException extends RuntimeException {
    
    private final int code;
    
    public TexasPokerException(String message) {
        super(message);
        this.code = 500;
    }
    
    public TexasPokerException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public TexasPokerException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
    
    public TexasPokerException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
