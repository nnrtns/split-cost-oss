package com.split.expenseSplitter.exception;

public class DuplicateInsertionException extends RuntimeException {
    public DuplicateInsertionException(String message) {
        super(message);
    }
}
