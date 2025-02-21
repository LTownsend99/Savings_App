package com.example.savings_app.exception;

/**
 * This class contains custom exception classes related to Milestone operations. It defines specific
 * exceptions for scenarios like milestone not found or invalid amount.
 */
public class MilestoneException {

  /** Exception thrown when a milestone is not found. */
  public static class MilestoneNotFoundException extends RuntimeException {

    /**
     * Constructor that accepts a custom message for the exception.
     *
     * @param message The custom message describing the error.
     */
    public MilestoneNotFoundException(String message) {
      super(message); // Pass the message to the superclass (RuntimeException)
    }
  }

  /** Exception thrown when an invalid amount is provided (e.g., negative or zero). */
  public static class InvalidAmountException extends RuntimeException {

    /**
     * Constructor that accepts a custom message for the exception.
     *
     * @param message The custom message describing the error.
     */
    public InvalidAmountException(String message) {
      super(message); // Pass the message to the superclass (RuntimeException)
    }
  }
}
