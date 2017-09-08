package com.implemica.CalculatorProject.model.exception;

/**
 * Thrown to indicate an errors occurred while mathematical calculations: division by zero, square root from
 * negative number or if result is overflow.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculationException extends Exception {

    private ErrorMessage errorMessage;

    /**
     * Constructs a new instance with the specified error errorMessage.
     *
     * @param errorMessage a errorMessage to describe an exception
     */
    public CalculationException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
