package com.implemica.CalculatorProject.model.exception;

/**
 * The {@code CalculationException} is the class that extends {@link Exception}. Designed
 * to represent an errors occurred while mathematical calculations such as division by zero.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculationException extends Exception {

    public CalculationException() {
        super();
    }

    /**
     * Constructs a new instance with the specified error message.
     *
     * @param message a message to describe an exception
     */
    public CalculationException(String message) {
        super(message);
    }
}
