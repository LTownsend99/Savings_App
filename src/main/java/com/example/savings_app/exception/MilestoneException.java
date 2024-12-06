package com.example.savings_app.exception;

public class MilestoneException {

    public static class MilestoneNotFoundException extends RuntimeException {
        public MilestoneNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidAmountException extends RuntimeException {
        public InvalidAmountException(String message) {
            super(message);
        }
    }
}
