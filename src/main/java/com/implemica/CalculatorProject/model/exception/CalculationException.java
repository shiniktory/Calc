package com.implemica.CalculatorProject.model.exception;

/**
 * Thrown to indicate TODO  mathematical calculations.
 * This exception throws in cases of division by zero, square root from negative number or if result is overflow.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculationException extends Exception {

    /**
     * Constructs a new instance with the specified error message.
     *
     * @param message a message to describe an exception
     */
    public CalculationException(String message) {
        super(message);
    }
}
