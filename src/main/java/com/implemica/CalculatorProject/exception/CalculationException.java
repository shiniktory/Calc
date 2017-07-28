package com.implemica.CalculatorProject.exception;

public class CalculationException extends Exception {

    public CalculationException() {
        super();
    }

    public CalculationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
